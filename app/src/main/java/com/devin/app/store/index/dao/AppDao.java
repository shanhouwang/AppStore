package com.devin.app.store.index.dao;

import com.devin.app.store.index.model.AppInfoDto;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Devin on 2018/1/16.
 */

public class AppDao {

    public static RealmResults<AppInfoDto> getDownloadedApps() {
        return Realm.getDefaultInstance().where(AppInfoDto.class).equalTo("downloadStatus", AppInfoDto.DOWNLOADED).findAll();
    }
}
