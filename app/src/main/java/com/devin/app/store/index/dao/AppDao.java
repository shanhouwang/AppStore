package com.devin.app.store.index.dao;

import com.devin.app.store.index.model.AppInfoDTO;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Devin on 2018/1/16.
 */

public class AppDAO {

    public static RealmResults<AppInfoDTO> getDownloadedApps() {
        return Realm.getDefaultInstance().where(AppInfoDTO.class).equalTo("downloadStatus", AppInfoDTO.DOWNLOADED).findAll();
    }
}