package com.devin.app.store.mine.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Devin on 2018/1/20.
 */

public class AppUpdateInfoDTO extends RealmObject {

    @PrimaryKey
    public int id;

    public String appName;

    public String oldVersion;

    public String newVersion;

    public long appSize;

    public String updateTime;

    public String updateDesc;

    public int downloadStatus;

    public String packageName;

    public int downloadPercent;

    public long downloadProgress;

    public String localPath;

    public String downloadUrl;

    @Ignore
    public static final int PREPARE_DOWNLOAD = 0;

    @Ignore
    public static final int DOWNLOADING = 1;

    @Ignore
    public static final int DOWNLOADED = 2;
}
