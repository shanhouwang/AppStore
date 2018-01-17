package com.devin.app.store.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseActivity;
import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.RealmUtils;
import com.devin.app.store.base.utils.ThreadUtils;
import com.devin.app.store.index.AppListAdapter;
import com.devin.app.store.index.dao.AppDAO;
import com.devin.app.store.index.model.AppInfoDTO;
import com.devin.app.store.search.dao.SearchDAO;
import com.devin.app.store.search.model.RecommandModel;
import com.devin.app.store.search.model.SearchHistoryDTO;
import com.devin.refreshview.MarsRefreshView;
import com.devin.refreshview.MercuryOnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Devin on 2018/1/17.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        initView();
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mMarsRefreshView = findViewById(R.id.marsRefreshView);
        et_search = findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changeSearchRecommAdapter();
                List<RecommandModel> data = new ArrayList<>();
                if (!TextUtils.isEmpty(et_search.getText().toString().trim())) {
                    for (int i = 0; i < 10; i++) {
                        RecommandModel model = new RecommandModel();
                        model.keyword = et_search.getText().toString().trim();
                        data.add(model);
                    }
                    mSearchRecommAdapter.initData(data);
                    mMarsRefreshView.onComplete();
                } else {
                    initHistoryData();
                }
            }
        });
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                final String keyword = et_search.getText().toString().trim();
                if (keyCode == KeyEvent.KEYCODE_ENTER && !TextUtils.isEmpty(keyword)) {
                    CommonUtils.hideSoftKeyboard(et_search);
                    changeAppListAdapter();
                    if (null == SearchDAO.getByKeyWord(keyword)) {
                        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                SearchHistoryDTO dto = realm.createObject(SearchHistoryDTO.class);
                                dto.keyWord = keyword;
                            }
                        });
                    }
                }
                return false;
            }
        });
        mSearchRecommAdapter = new SearchRecommAdapter(this);
        mMarsRefreshView.setLinearLayoutManager()
                .setAdapter(mSearchRecommAdapter);
        initHistoryData();
    }

    private MarsRefreshView mMarsRefreshView;
    private EditText et_search;
    private SearchRecommAdapter mSearchRecommAdapter;
    private AppListAdapter mAppListAdapter;

    private void changeSearchRecommAdapter() {
        if (mMarsRefreshView.getRecyclerView().getAdapter() instanceof SearchRecommAdapter) {
            return;
        }
        mMarsRefreshView.setLinearLayoutManager()
                .setAdapter(mSearchRecommAdapter);
    }

    /**
     * 点击了历史、搜索以及关键字
     */
    public void changeAppListAdapter() {
        mAppListAdapter = new AppListAdapter(this);
        mMarsRefreshView.setAdapter(mAppListAdapter)
                .setMercuryOnLoadMoreListener(1, new MercuryOnLoadMoreListener() {
                    @Override
                    public void onLoadMore(final int page) {
                        if (page == 5) {
                            mMarsRefreshView.onComplete();
                            return;
                        }
                        ThreadUtils.get(ThreadUtils.Type.SCHEDULED).callBack(new ThreadUtils.CallBack() {
                            @Override
                            public void onResponse(Object obj) {
                                mAppListAdapter.bindLoadMoreData((List<AppInfoDTO>) obj);
                            }
                        }).schedule(new ThreadUtils.MyRunnable() {
                            @Override
                            public Object execute() {
                                List<AppInfoDTO> models = new ArrayList<>();
                                for (int i = 10 * page; i < 10 * page + 10; i++) {
                                    AppInfoDTO appInfoDTO = new AppInfoDTO();
                                    appInfoDTO.id = i;
                                    appInfoDTO.appName = "应用名称 " + i;
                                    appInfoDTO.rating = i % 5;
                                    appInfoDTO.appSize = 10 + i;
                                    appInfoDTO.appClassify = "金融理财";
                                    appInfoDTO.appDesc = "金融理财的好帮手，年利率10%以上产品很多";
                                    appInfoDTO.downloadUrl = "http://imtt.dd.qq.com/16891/F85076B8EA32D933089CEA797CF38C30.apk";
                                    models.add(appInfoDTO);
                                }
                                return models;
                            }
                        }, 1 * 1000, TimeUnit.MILLISECONDS);
                    }
                });
        initAppsData();
    }

    private void initAppsData() {
        showProgress();
        ThreadUtils.get(ThreadUtils.Type.SCHEDULED).callBack(new ThreadUtils.CallBack() {
            @Override
            public void onResponse(Object obj) {
                hideProgress();
                mAppListAdapter.initData((List<AppInfoDTO>) obj);
                updateStatus((List<AppInfoDTO>) obj);
            }
        }).schedule(new ThreadUtils.MyRunnable() {
            @Override
            public Object execute() {
                List<AppInfoDTO> models = new ArrayList<>();
                for (int i = 1; i < 11; i++) {
                    AppInfoDTO appInfoDTO = new AppInfoDTO();
                    appInfoDTO.id = i;
                    appInfoDTO.appName = "应用名称 " + i;
                    appInfoDTO.rating = i % 5;
                    appInfoDTO.appSize = 10 + i;
                    appInfoDTO.appClassify = "金融理财";
                    appInfoDTO.appDesc = "金融理财的好帮手，年利率10%以上产品很多";
                    appInfoDTO.downloadUrl = "http://imtt.dd.qq.com/16891/F85076B8EA32D933089CEA797CF38C30.apk";
                    if (i == 1) {
                        appInfoDTO.packageName = "com.tencent.qqlive";
                        appInfoDTO.downloadUrl = "http://imtt.dd.qq.com/16891/110A36BF492C6672528F40A4FFDB22B4.apk";
                    }
                    if (i == 2) {
                        appInfoDTO.packageName = "com.cleanmaster.mguard_cn";
                        appInfoDTO.downloadUrl = "http://imtt.dd.qq.com/16891/3CC768370B43EDF35F56BB3948C77BA8.apk";
                    }
                    models.add(appInfoDTO);
                }
                return models;
            }
        }, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    private void updateStatus(final List<AppInfoDTO> appsOfWeb) {
        ThreadUtils.get(ThreadUtils.Type.CACHED).callBack(new ThreadUtils.CallBack() {
            @Override
            public void onResponse(Object obj) {
                if (obj == null) {
                    return;
                }
                List<Integer> notifyPositions = (List<Integer>) obj;
                for (int i = 0; i < notifyPositions.size(); i++) {
                    mAppListAdapter.notifyItemChanged(notifyPositions.get(i), R.id.tv_install);
                }
            }
        }).run(new ThreadUtils.MyRunnable() {
            @Override
            public Object execute() {
                RealmResults<AppInfoDTO> appsOfDb = AppDAO.getDownloadedApps();
                List<Integer> notifyPositions = new ArrayList<>();
                for (int x = 0; x < appsOfWeb.size(); x++) {
                    AppInfoDTO appOfWeb = appsOfWeb.get(x);
                    for (int y = 0; y < appsOfDb.size(); y++) {
                        AppInfoDTO appOfDb = appsOfDb.get(y);
                        if (appOfWeb.id == appOfDb.id
                                && CommonUtils.isOkFile(appOfDb.localPath, appOfDb.appSize)) {
                            appOfWeb.downloadStatus = AppInfoDTO.DOWNLOADED;
                            appOfWeb.localPath = appOfDb.localPath;
                            notifyPositions.add(x);
                        }
                    }
                }
                return notifyPositions;
            }
        });
    }

    private void initHistoryData() {
        ThreadUtils.get(ThreadUtils.Type.CACHED).callBack(new ThreadUtils.CallBack() {
            @Override
            public void onResponse(Object obj) {
                if (obj != null && ((List<RecommandModel>) obj).size() > 0) {
                    mSearchRecommAdapter.initData((List<RecommandModel>) obj);
                    mMarsRefreshView.onComplete();
                }
            }
        }).run(new ThreadUtils.MyRunnable() {
            @Override
            public Object execute() {
                RealmResults<SearchHistoryDTO> dtos = SearchDAO.getHistory();
                if (dtos == null) {
                    return null;
                }
                List<RecommandModel> history = new ArrayList<>();
                for (int i = 0; i < dtos.size(); i++) {
                    RecommandModel model = new RecommandModel();
                    model.keyword = dtos.get(i).keyWord;
                    model.formHistory = true;
                    history.add(model);
                }
                return history;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}
