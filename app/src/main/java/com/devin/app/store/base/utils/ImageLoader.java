package com.devin.app.store.base.utils;

import android.content.Context;
import android.widget.ImageView;

import com.devin.app.store.R;
import com.devin.app.store.base.config.GlideApp;
import com.devin.app.store.base.config.GlideRoundTransform;

/**
 * Created by Devin on 17/6/22.
 * <p>
 * 图片加载
 */

public class ImageLoader {

    /**
     * @param context 上下文
     * @param iv
     * @param url
     * @param place   占位图
     */
    public static void get(Context context, ImageView iv, String url, int place) {
        GlideApp.with(context)
                .load(url)
                .placeholder(place)
                .error(place)
                .into(iv);
    }

    /**
     * 默认占位图
     *
     * @param context 上下文
     * @param iv
     * @param url
     */
    public static void get(Context context, ImageView iv, String url) {
        GlideApp.with(context)
                .load(url)
                //.placeholder()
                //.error()
                .into(iv);
    }

    /**
     * @param context
     * @param iv
     * @param url     图片地址
     * @param dp      圆角半径
     */
    public static void getRound(Context context, final ImageView iv, String url, final int dp) {
        GlideApp.with(context)
                .load(url)
                //.placeholder()
                //.error()
                .centerCrop()
                .transform(new GlideRoundTransform(context, dp))
                .into(iv);
    }

    /**
     * 圆图片
     *
     * @param context
     * @param iv
     * @param url     图片地址
     */
    public static void getCircle(Context context, final ImageView iv, String url) {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.mipmap.default_avatar)
                .error(R.mipmap.default_avatar)
                .circleCrop()
                .into(iv);
    }

    /**
     * 圆图片
     *
     * @param context
     * @param iv
     * @param url     图片地址
     */
    public static void getCircle(Context context, final ImageView iv, String url, int place) {
        GlideApp.with(context)
                .load(url)
                .placeholder(place)
                .error(place)
                .circleCrop()
                .into(iv);
    }
}
