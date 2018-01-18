package com.devin.app.store.index.dao;

import com.devin.app.store.index.model.AppInfoDTO;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Devin on 2018/1/16.
 */

public class AppDAO {

    public static void getDownloadedApps(Realm realm, RealmChangeListener<RealmResults<AppInfoDTO>> listener) {
        realm.where(AppInfoDTO.class).equalTo("downloadStatus", AppInfoDTO.DOWNLOADED).findAllAsync().addChangeListener(listener);
    }
}