package com.devin.app.store.mine.dao;

import com.devin.app.store.mine.model.AppUpdateInfoDTO;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Devin on 2018/1/20.
 *
 * @author Devin
 */
public class UpdateDAO {

    public static AppUpdateInfoDTO getModelById(Realm realm, int id) {
        return realm.where(AppUpdateInfoDTO.class).equalTo("id", id).findFirst();
    }

    public static void getAppsByStatus(Realm realm, RealmChangeListener<RealmResults<AppUpdateInfoDTO>> listener) {
        realm.where(AppUpdateInfoDTO.class).equalTo("downloadStatus", AppUpdateInfoDTO.DOWNLOADED).findAllAsync().addChangeListener(listener);
    }
}
