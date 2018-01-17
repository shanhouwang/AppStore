package com.devin.app.store.search.dao;

import com.devin.app.store.search.model.SearchHistoryDTO;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Devin on 2018/1/17.
 */

public class SearchDAO {

    /**
     * 获取历史搜索记录
     *
     * @return
     */
    public static RealmResults<SearchHistoryDTO> getHistory() {
        return Realm.getDefaultInstance().where(SearchHistoryDTO.class).findAll();
    }

    public static SearchHistoryDTO getByKeyWord(String keyWord) {
        return Realm.getDefaultInstance().where(SearchHistoryDTO.class).equalTo("keyWord", keyWord).findFirst();
    }
}
