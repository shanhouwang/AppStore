package com.devin.app.store.base.utils;

import android.os.Environment;

/**
 * Created by Devin on 17/6/23.
 */

public class SDCardUtils {

    /**
     * SD卡是否挂载
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
}
