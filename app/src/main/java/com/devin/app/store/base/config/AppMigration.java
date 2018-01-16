package com.devin.app.store.base.config;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * Created by Devin on 2018/1/16.
 */

public class AppMigration implements RealmMigration{
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
    }
}
