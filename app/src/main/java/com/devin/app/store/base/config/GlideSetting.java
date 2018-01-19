package com.devin.app.store.base.config;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.utils.SDCardUtils;

import java.io.File;

/**
 * Created by Devin on 17/6/22.
 */
@GlideModule
public class GlideSetting extends AppGlideModule {

    public static final String GLIDE_CACHE = "glideCache";

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // 设置图片的显示格式ARGB_8888(指图片大小为32bit)
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        // 设置磁盘缓存目录（和创建的缓存目录相同）
        String path;
        if (SDCardUtils.isSDCardEnable()) {
            path = BaseApp.app.getExternalCacheDir() + File.separator + GLIDE_CACHE + File.separator;
        } else {
            path = BaseApp.app.getCacheDir() + File.separator + GLIDE_CACHE + File.separator;
        }
        // 设置缓存的大小为100M
        int cacheSize = 100 * 1000 * 1000;
        builder.setDiskCache(new DiskLruCacheFactory(path, cacheSize));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
