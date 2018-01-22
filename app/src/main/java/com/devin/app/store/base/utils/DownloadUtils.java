package com.devin.app.store.base.utils;

import com.devin.app.store.base.BaseApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Devin on 17/4/27.
 */
public class DownloadUtils {

    public static List<String> requestUrls = new ArrayList<>();

    /**
     * 下载文件
     *
     * @param url        地址
     * @param fileName   文件名称
     * @param progress   是否有Notification
     * @param breakPoint 是否断点下载
     * @param callBack   回调
     */
    public static void downAsynFile(final String url, final String fileName, boolean progress, DownloadUtils.BreakPoint breakPoint, final DownloadCallBack callBack) {
        // 多次请求只允许一次
        synchronized (requestUrls) {
            if (requestUrls.contains(url)) {
                return;
            }
            requestUrls.add(url);
        }

        Request request;
        if (null != breakPoint && breakPoint.progressLength != 0) {
            request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + breakPoint.progressLength + "-" + breakPoint.contentLength)
                    .url(url)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }
        request(url, fileName, progress, callBack, request);
    }

    private static void request(String url, String fileName, boolean progress, DownloadCallBack callBack, Request request) {
        BaseApp.mOkhttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onResponse(null);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                long contentLength = response.body().contentLength();
                FileOutputStream fileOutputStream;
                String localPath = getLocalFilePath(fileName);
                File f = new File(localPath);
                CallBackBean bean;
                try {
                    fileOutputStream = new FileOutputStream(f);
                    byte[] buffer = new byte[1024 * 1024];
                    int len;
                    long total = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                        if (progress && contentLength > 0) {
                            total += len;
                            if (callBack != null) {
                                bean = new CallBackBean();
                                bean.path = localPath;
                                bean.isNeedProgress = progress;
                                bean.max = contentLength;
                                bean.progressLength = total;
                                callBack.onResponse(bean);
                            }
                        }
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    // 发生了异常要Remove
                    requestUrls.remove(url);
                }
                LogUtils.d(">>>>>localPath:" + localPath);
                if (callBack != null && !progress) {
                    bean = new CallBackBean();
                    bean.path = localPath;
                    bean.isNeedProgress = progress;
                    callBack.onResponse(bean);
                }
                // 下载完了删除此Url
                requestUrls.remove(url);
            }
        });
    }

    /**
     * 获取线上File大小
     *
     * @param url
     * @param callBack
     */
    public static void getAsynFileLength(final String url, final DownloadCallBack callBack) {
        Request request = new Request.Builder().url(url).build();
        BaseApp.mOkhttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null) {
                    callBack.onResponse(null);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack != null) {
                    CallBackBean bean = new CallBackBean();
                    bean.max = (int) response.body().contentLength();
                    callBack.onResponse(bean);
                }
            }
        });
    }

    /**
     * 本地文件缓存地址
     *
     * @param fileName
     * @return
     */
    public static String getLocalFilePath(String fileName) {
        String path = BaseApp.app.getExternalCacheDir().getAbsolutePath() + File.separator + fileName;
        return path;
    }

    public interface DownloadCallBack {

        /**
         * progress 为 true 会被频繁调用
         *
         * @param bean
         */
        void onResponse(CallBackBean bean);
    }


    public static class CallBackBean {
        /**
         * 下载后 存储到本地的Url
         */
        public String path;

        /**
         * 是否需要进度
         */
        public boolean isNeedProgress;

        /**
         * 最大值
         */
        public long max;

        /**
         * 每次更新进度
         */
        public long progressLength;
    }

    public static class BreakPoint implements Serializable{

        public long progressLength;
        public long contentLength;

        public BreakPoint(long progressLength, long contentLength) {
            this.progressLength = progressLength;
            this.contentLength = contentLength;
        }

    }

}
