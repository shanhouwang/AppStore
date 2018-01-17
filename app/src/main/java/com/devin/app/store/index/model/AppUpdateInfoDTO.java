package com.devin.app.store.index.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Devin on 2018/1/11.
 */

public class AppUpdateInfoDTO extends RealmObject {

    @PrimaryKey
    public int id;

    public String appName;

    /**
     * 老版本号
     */
    public String versionNameOld;

    /**
     * 新版本号
     */
    public String versionNameUpdate;

    /**
     * 更新时间
     */
    public long updateTime;

    /**
     * 更新内容
     */
    public String updateInfo;

}