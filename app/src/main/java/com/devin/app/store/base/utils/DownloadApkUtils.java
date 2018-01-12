package com.devin.app.store.base.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.widget.MyToast;

import java.io.File;

/**
 * Created by Devin on 17/5/1.
 */

public class DownloadApkUtils {

    private Activity mActivity;
    private DownloadUtils.DownloadCallBack callBack;

    public static DownloadApkUtils get(Activity activity, DownloadUtils.DownloadCallBack callBack) {
        DownloadApkUtils util = new DownloadApkUtils();
        util.mActivity = activity;
        util.callBack = callBack;
        return util;
    }

    /**
     * @param url 下载的Url
     */
    public void download(final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!CommonUtils.isValidUrl(url)) {
            MyToast.show(BaseApp.app, "下载的Url不合法");
            return;
        }
        String path = BaseApp.sp.getString(url);
        final String fileName = CommonUtils.getFileName(url);
        if (TextUtils.isEmpty(path)) {
            showWarningDialog(url, fileName);
            return;
        }

        final File apk = new File(path);
        if (!apk.exists()) {
            showWarningDialog(url, fileName);
            return;
        }

        DownloadUtils.getAsynFileLength(url, new DownloadUtils.DownloadCallBack() {
            @Override
            public void onResponse(DownloadUtils.CallBackBean bean) {
                if (bean != null && (bean.max == apk.length())) {
                    mActivity.startActivity(getIntent(BaseApp.sp.getString(url)));
                } else if (bean != null && (bean.max != apk.length())) {
                    showWarningDialog(url, fileName);
                }
                return;
            }
        });
    }

    /**
     * 非WIFI网络显示警告Dialog
     *
     * @param url
     * @param fileName
     */
    private void showWarningDialog(final String url, final String fileName) {
        if (NetworkUtils.getWifiEnabled()) {
            doIt(url, fileName);
            return;
        }
        new AlertDialog.Builder(mActivity)
                .setTitle("现在更新吗？")
                .setMessage("您的手机当前没有连接WIFI，现在更新会消耗您的手机流量，您确定要现在更新吗？")
                .setNegativeButton("待会儿再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doIt(url, fileName);
                    }
                })
                .create()
                .show();
    }

    private void doIt(String url, String fileName) {
        DownloadUtils.downAsynFile(url, fileName, true, callBack);
    }

    public static Intent getIntent(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(BaseApp.app, "com.devin.app.store.FileProvider", new File(path));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

}
