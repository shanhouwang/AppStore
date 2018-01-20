package com.devin.app.store.classify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.classify.model.ClassifyModel;
import com.devin.refreshview.MarsRefreshView;
import com.devin.refreshview.MeasureUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Devin on 2018/1/9.
 * <p>
 * 分类
 */
public class ClassifyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.classify_fragment, null);
    }

    private MarsRefreshView mMarsRefreshView;
    private ClassifyAdapter adapter;
    private ProgressBar progressbar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View v) {
        mMarsRefreshView = v.findViewById(R.id.marsRefreshView);
        progressbar = v.findViewById(R.id.progressbar);
        ((TextView) v.findViewById(R.id.tv_title)).setText("应用分类");
        FrameLayout toolbarLayout = v.findViewById(R.id.tool_bar_layout);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) toolbarLayout.getLayoutParams();
        params.topMargin = MeasureUtils.dp2px(getContext(),25);
        toolbarLayout.setLayoutParams(params);
        adapter = new ClassifyAdapter(getContext());
        mMarsRefreshView.setLinearLayoutManager()
                .setAdapter(adapter);

        Observable.create((ObservableEmitter<List<ClassifyModel>> subscriber) -> {

            List<ClassifyModel> models = new ArrayList<>();

            for (int i = 0; i < 15; i++) {
                ClassifyModel model = new ClassifyModel();
                if (0 == i) {
                    model.name = "影音视听";
                    model.imgUrl = R.drawable.ic_media;
                } else if (1 == i) {
                    model.name = "实用工具";
                    model.imgUrl = R.drawable.ic_tools;
                } else if (2 == i) {
                    model.name = "聊天社交";
                    model.imgUrl = R.drawable.ic_chat;
                } else if (3 == i) {
                    model.name = "图书书店";
                    model.imgUrl = R.drawable.ic_reader;
                } else if (4 == i) {
                    model.name = "时尚购物";
                    model.imgUrl = R.drawable.ic_shopping;
                } else if (5 == i) {
                    model.name = "摄影摄像";
                    model.imgUrl = R.drawable.ic_photo;
                } else if (6 == i) {
                    model.name = "学习教育";
                    model.imgUrl = R.drawable.ic_education;
                } else if (7 == i) {
                    model.name = "旅行交通";
                    model.imgUrl = R.drawable.ic_travel;
                } else if (8 == i) {
                    model.name = "金融理财";
                    model.imgUrl = R.drawable.ic_manage_money;
                } else if (9 == i) {
                    model.name = "娱乐消遣";
                    model.imgUrl = R.drawable.ic_entertainment;
                } else if (10 == i) {
                    model.name = "新闻资讯";
                    model.imgUrl = R.drawable.ic_news;
                } else if (11 == i) {
                    model.name = "居家生活";
                    model.imgUrl = R.drawable.ic_life;
                } else if (12 == i) {
                    model.name = "体育运动";
                    model.imgUrl = R.drawable.ic_sport;
                } else if (13 == i) {
                    model.name = "医疗健康";
                    model.imgUrl = R.drawable.ic_treatment;
                } else if (14 == i) {
                    model.name = "效率办公";
                    model.imgUrl = R.drawable.ic_office;
                }
                List<ClassifyModel> seconds = new ArrayList<>();
                for (int x = 0; x < 6; x++) {
                    ClassifyModel m = new ClassifyModel();
                    m.name = "视频";
                    seconds.add(m);
                }
                model.secondClassify = seconds;
                models.add(model);
            }
            subscriber.onNext(models);

        })
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(models -> {
                    progressbar.setVisibility(View.GONE);
                    adapter.initData(models);
                    mMarsRefreshView.onComplete();
                });
    }
}
