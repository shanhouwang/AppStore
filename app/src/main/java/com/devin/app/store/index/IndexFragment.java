package com.devin.app.store.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.devin.app.store.R;
import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.ThreadUtils;
import com.devin.app.store.index.dao.AppDao;
import com.devin.app.store.index.model.AppInfoDto;
import com.devin.app.store.search.SearchActivity;
import com.devin.refreshview.MarsRefreshView;
import com.devin.refreshview.MercuryOnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.RealmResults;

/**
 * Created by Devin on 2018/1/9.
 * <p>
 * 推荐页面
 */
public class IndexFragment extends Fragment implements View.OnClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.index_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIndexAdapter.notifyItemChanged(IndexAdapter.CLICK_POSITION, R.id.tv_install);
    }

    private MarsRefreshView mMarsRefreshView;
    private ProgressBar progressbar;
    private IndexAdapter mIndexAdapter;
    private IndexBaseAdapter mIndexBaseAdapter;

    private void initView(View v) {
        v.findViewById(R.id.layout_search).setOnClickListener(this);
        mMarsRefreshView = v.findViewById(R.id.marsRefreshView);
        progressbar = v.findViewById(R.id.progressbar);
        mIndexAdapter = new IndexAdapter(getContext());
        mIndexBaseAdapter = new IndexBaseAdapter(getContext());

        mMarsRefreshView
                .setLinearLayoutManager()
                .setAdapter(mIndexAdapter)
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
                                mIndexAdapter.bindLoadMoreData((List<AppInfoDto>) obj);
                                mIndexBaseAdapter.bindLoadMoreData((List<AppInfoDto>) obj);
                            }
                        }).schedule(new ThreadUtils.MyRunnable() {
                            @Override
                            public Object execute() {
                                List<AppInfoDto> models = new ArrayList<>();
                                for (int i = 10 * page; i < 10 * page + 10; i++) {
                                    AppInfoDto appInfoDto = new AppInfoDto();
                                    appInfoDto.id = i;
                                    appInfoDto.appName = "应用名称 " + i;
                                    appInfoDto.rating = i % 5;
                                    appInfoDto.appSize = 10 + i;
                                    appInfoDto.appClassify = "金融理财";
                                    appInfoDto.appDesc = "金融理财的好帮手，年利率10%以上产品很多";
                                    appInfoDto.downloadUrl = "http://imtt.dd.qq.com/16891/F85076B8EA32D933089CEA797CF38C30.apk";
                                    models.add(appInfoDto);
                                }
                                return models;
                            }
                        }, 1 * 1000, TimeUnit.MILLISECONDS);
                    }
                });

        initData();
    }

    private void initData() {
        progressbar.setVisibility(View.VISIBLE);
        ThreadUtils.get(ThreadUtils.Type.SCHEDULED).callBack(new ThreadUtils.CallBack() {
            @Override
            public void onResponse(Object obj) {
                progressbar.setVisibility(View.GONE);
                mIndexAdapter.initData((List<AppInfoDto>) obj);
                mIndexBaseAdapter.initData((List<AppInfoDto>) obj);
                updateStatus((List<AppInfoDto>) obj);
            }
        }).schedule(new ThreadUtils.MyRunnable() {
            @Override
            public Object execute() {
                List<AppInfoDto> models = new ArrayList<>();
                for (int i = 1; i < 11; i++) {
                    AppInfoDto appInfoDto = new AppInfoDto();
                    appInfoDto.id = i;
                    appInfoDto.appName = "应用名称 " + i;
                    appInfoDto.rating = i % 5;
                    appInfoDto.appSize = 10 + i;
                    appInfoDto.appClassify = "金融理财";
                    appInfoDto.appDesc = "金融理财的好帮手，年利率10%以上产品很多";
                    appInfoDto.downloadUrl = "http://imtt.dd.qq.com/16891/F85076B8EA32D933089CEA797CF38C30.apk";
                    if (i == 1) {
                        appInfoDto.packageName = "com.tencent.qqlive";
                        appInfoDto.downloadUrl = "http://imtt.dd.qq.com/16891/110A36BF492C6672528F40A4FFDB22B4.apk";
                    }
                    if (i == 2) {
                        appInfoDto.packageName = "com.cleanmaster.mguard_cn";
                        appInfoDto.downloadUrl = "http://imtt.dd.qq.com/16891/3CC768370B43EDF35F56BB3948C77BA8.apk";
                    }
                    models.add(appInfoDto);
                }
                return models;
            }
        }, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    private void updateStatus(final List<AppInfoDto> appsOfWeb) {
        ThreadUtils.get(ThreadUtils.Type.CACHED).callBack(new ThreadUtils.CallBack() {
            @Override
            public void onResponse(Object obj) {
                if (obj == null) {
                    return;
                }
                List<Integer> notifyPositions = (List<Integer>) obj;
                for (int i = 0; i < notifyPositions.size(); i++) {
                    mIndexAdapter.notifyItemChanged(notifyPositions.get(i), R.id.tv_install);
                }
            }
        }).run(new ThreadUtils.MyRunnable() {
            @Override
            public Object execute() {
                RealmResults<AppInfoDto> appsOfDb = AppDao.getDownloadedApps();
                List<Integer> notifyPositions = new ArrayList<>();
                for (int x = 0; x < appsOfWeb.size(); x++) {
                    AppInfoDto appOfWeb = appsOfWeb.get(x);
                    for (int y = 0; y < appsOfDb.size(); y++) {
                        AppInfoDto appOfDb = appsOfDb.get(y);
                        if (appOfWeb.id == appOfDb.id
                                && CommonUtils.isOkFile(appOfDb.localPath, appOfDb.appSize)) {
                            appOfWeb.downloadStatus = AppInfoDto.DOWNLOADED;
                            appOfWeb.localPath = appOfDb.localPath;
                            notifyPositions.add(x);
                        }
                    }
                }
                return notifyPositions;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_search:
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
                break;
        }
    }
}
