package com.devin.app.store.base.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.config.Config;

import java.io.File;
import java.util.List;

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
     * 得到Okhttp的缓存目录
     *
     * @return
     */
    public static File getRealmDirectory() {
        if (SDCardUtils.isSDCardEnable()) {
            String path = BaseApp.app.getExternalCacheDir() + File.separator + Config.REALM_CACHE + File.separator;
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

    /**
     * 判断文件存在否、被损坏否
     *
     * @param path
     * @param fileSize
     * @return
     */
    public static boolean isOkFile(String path, int fileSize) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        if (f.length() == fileSize) {
            return true;
        }
        return false;
    }

    /**
     * 检测应用是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        if (null == info) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 打开第三方App
     *
     * @param context
     * @param packageName
     * @throws PackageManager.NameNotFoundException
     */
    public static void openApp(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(packageName, 0);
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String pn = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(pn, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }
}