package com.devin.app.store.search.dao;

import com.devin.app.store.search.model.SearchHistoryDTO;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Devin on 2018/1/17.
 *
 * @author Devin
 */
public class SearchDAO {

    /**
     * 获取历史搜索记录
     *
     * @return
     */
    public static RealmResults<SearchHistoryDTO> getHistory(Realm realm) {
        return realm.where(SearchHistoryDTO.class).findAll().sort("time", Sort.DESCENDING);
    }

    public static SearchHistoryDTO getByKeyWord(Realm realm, String keyWord) {
        return realm.where(SearchHistoryDTO.class).equalTo("keyWord", keyWord).findFirst();
    }
}
