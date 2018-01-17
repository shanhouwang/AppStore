package com.devin.app.store.search.model;

import io.realm.RealmObject;

/**
 * Created by Devin on 2018/1/17.
 */

public class SearchHistoryDTO extends RealmObject {

    public String keyWord;

    public long time;

}
