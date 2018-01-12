package com.devin.app.store.base.utils;

import android.content.Context;

/**
 * Created by Devin on 2018/1/11.
 */

public class Utils {

    public static Context context;

    public static void init(Context context) {
        Utils.context = context;
    }

    public static Context getContext() {
        return context;
    }
}
