package com.devin.app.store.mine.dao;

import com.devin.app.store.mine.model.AppUpdateInfoDTO;

import io.realm.Realm;

/**
 * Created by Devin on 2018/1/20.
 */

public class UpdateDAO {

    public static AppUpdateInfoDTO getModelById(Realm realm, int id) {
        return realm.where(AppUpdateInfoDTO.class).equalTo("id", id).findFirst();
    }
}
