package com.devin.app.store.base.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.config.Config;

import java.io.File;

public class CommonUtils {

    /**
     * 得到Okhttp的缓存目录
     *
     * @return
     */
    public static File getOkhttpCache() {
        if (SDCardUtils.isSDCardEnable()) {
            String path = BaseApp.app.getExternalCacheDir() + File.separator + Config.OKHTTP_CACHE + File.separator;
            File f = new File(path);
            boolean flag = f.mkdirs();
            if (flag) {
                return f;
            } else {
                return BaseApp.app.getCacheDir();
            }
        } else {
            return BaseApp.app.getCacheDir();
        }
    }

    /**
     * 传入格式 http://www.tp.com/tp.apk
     * <p>
     * 返回 tp.apk
     */
    public static String getFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String fileName = null;
        int begin = url.lastIndexOf("/");
        if (begin != -1) {
            fileName = url.substring(begin + 1, url.length());
        }
        return fileName;
    }

    /**
     * 检测Url的合法性
     *
     * @param url
     * @return
     */
    public static boolean isValidUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        if ((url.startsWith("http://") || url.startsWith("https://"))
                && url.endsWith(".apk")) {
            return true;
        }
        return false;
    }

    private static InputMethodManager mInputMethodManager;

    /**
     * 隐藏软键盘
     *
     * @param activity
     * @return
     */
    public static boolean hideSoftKeyboard(Activity activity) {
        if (mInputMethodManager == null) {
            mInputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (mInputMethodManager.isActive()) {
            try {
                mInputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        return false;
    }
}