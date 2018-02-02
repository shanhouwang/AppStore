package com.devin.app.store.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.devin.app.store.base.config.AppMigration;
import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.SPUtils;
import com.devin.app.store.base.utils.Utils;
import com.devin.downloader.MercuryDownloader;

import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Devin on 2018/1/9.
 */

public class BaseApp extends Application {

    public static Application app;
    public static OkHttpClient mOkhttp;
    public static Handler mHandler = new Handler(Looper.getMainLooper());
    public static SPUtils sp;
    public static final String SP_NAME = "AppStore.sp";

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        Utils.init(this);

        mOkhttp = new OkHttpClient.Builder()
                .cache(new Cache(CommonUtils.getOkhttpCache(), 1024 * 1024 * 20))
                .connectTimeout(30000L, TimeUnit.MILLISECONDS)
                .readTimeout(30000L, TimeUnit.MILLISECONDS)
                .build();
        sp = new SPUtils(SP_NAME);

        Realm.init(this);

        Realm.setDefaultConfiguration(new RealmConfiguration
                .Builder()
                .directory(CommonUtils.getRealmDirectory())
                .schemaVersion(1)
                .migration(new AppMigration())
                .deleteRealmIfMigrationNeeded()
                .build());

        MercuryDownloader.init(this, mOkhttp);

    }
}
