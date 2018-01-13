package com.devin.app.store.index;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.devin.app.store.R;
import com.devin.app.store.base.utils.ThreadUtils;
import com.devin.app.store.index.model.AppModel;
import com.devin.refreshview.MarsRefreshView;
import com.devin.refreshview.MercuryOnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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


    private MarsRefreshView mMarsRefreshView;
    private ProgressBar progressbar;
    private IndexAdapter mIndexAdapter;
    private IndexBaseAdapter mIndexBaseAdapter;
    private volatile List<AppModel> models = new ArrayList<>();

    private void initView(View v) {
        v.findViewById(R.id.layout_search).setOnClickListener(this);
        mMarsRefreshView = v.findViewById(R.id.marsRefreshView);
        progressbar = v.findViewById(R.id.progressbar);
        mIndexAdapter = new IndexAdapter(getContext());
        mIndexBaseAdapter = new IndexBaseAdapter(getContext());
        mMarsRefreshView
                .setAdapter(mIndexBaseAdapter)
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
                                mIndexAdapter.bindLoadMoreData((List<AppModel>) obj);
                                mIndexBaseAdapter.bindLoadMoreData((List<AppModel>) obj);
                            }
                        }).schedule(new ThreadUtils.MyRunnable() {
                            @Override
                            public Object execute() {
                                List<AppModel> models = new ArrayList<>();
                                for (int i = 10 * page; i < 10 * page + 10; i++) {
                                    AppModel appModel = new AppModel();
                                    appModel.appName = "应用名称 " + i;
                                    appModel.rating = i % 5;
                                    appModel.appSize = 10 + i;
                                    appModel.appClassify = "金融理财";
                                    appModel.appDesc = "金融理财的好帮手，年利率10%以上产品很多";
                                    appModel.downloadUrl = "http://imtt.dd.qq.com/16891/F85076B8EA32D933089CEA797CF38C30.apk";
                                    models.add(appModel);
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
                mIndexAdapter.initData(models);
                mIndexBaseAdapter.initData(models);
            }
        }).schedule(new ThreadUtils.MyRunnable() {
            @Override
            public Object execute() {
                for (int i = 0; i < 10; i++) {
                    AppModel appModel = new AppModel();
                    appModel.appName = "应用名称 " + i;
                    appModel.rating = i % 5;
                    appModel.appSize = 10 + i;
                    appModel.appClassify = "金融理财";
                    appModel.appDesc = "金融理财的好帮手，年利率10%以上产品很多";
                    appModel.downloadUrl = "http://imtt.dd.qq.com/16891/F85076B8EA32D933089CEA797CF38C30.apk";
                    if (i == 1) {
                        appModel.downloadUrl = "http://imtt.dd.qq.com/16891/110A36BF492C6672528F40A4FFDB22B4.apk";
                    }
                    if (i == 2) {
                        appModel.downloadUrl = "http://imtt.dd.qq.com/16891/3CC768370B43EDF35F56BB3948C77BA8.apk";
                    }
                    models.add(appModel);
                }
                return null;
            }
        }, 1 * 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_search:
                break;
        }
    }
}
