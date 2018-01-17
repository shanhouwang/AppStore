package com.devin.app.store.base.utils;

import io.realm.Realm;

/**
 * Created by Devin on 2018/1/17.
 */

public class RealmUtils {

    public static void create(Realm.Transaction transaction) {

        Realm realm= Realm.getDefaultInstance();

        realm.executeTransaction(transaction);

        realm.close();
    }
}
