package com.devin.app.store.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.base.utils.TimeUtils;
import com.devin.app.store.mine.dao.UpdateDAO;
import com.devin.app.store.mine.model.AppUpdateInfoDTO;
import com.devin.refreshview.MarsRefreshView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by Devin on 2018/1/9.
 * <p>
 * 应用提醒
 *
 * @author Devin
 */
public class MineFragment extends Fragment {

    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.mine_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private MarsRefreshView marsRefreshView;
    private AppUpdateListAdapter adapter;
    private ProgressBar progressbar;

    private void initView(View v) {
        ((TextView) v.findViewById(R.id.tv_title)).setText("应用更新");
        marsRefreshView = v.findViewById(R.id.marsRefreshView);
        progressbar = v.findViewById(R.id.progressbar);
        adapter = new AppUpdateListAdapter(getContext(), realm);
        marsRefreshView.setLinearLayoutManager()
                .setAdapter(adapter);
        initData();
    }

    private void initData() {
        progressbar.setVisibility(View.VISIBLE);
        Observable.create((ObservableEmitter<List<AppUpdateInfoDTO>> emitter) -> {
            List<AppUpdateInfoDTO> dtos = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                AppUpdateInfoDTO info = new AppUpdateInfoDTO();
                info.id = i;
                info.appName = "应用名称 " + i;
                info.appSize = 10 + i;
                info.oldVersion = "V1.0";
                info.newVersion = "V1.1";
                info.updateTime = "更新时间：" + TimeUtils.long2String(System.currentTimeMillis(), "yyyy年MM月dd日");
                info.updateDesc = "更新说明：\n1、修改BUG\n2、修改BUG\n3、修改BUG\n4、修改BUG\n6、修改BUG";
                info.downloadUrl = "http://imtt.dd.qq.com/16891/F85076B8EA32D933089CEA797CF38C30.apk";
                if (i == 1) {
                    info.packageName = "com.tencent.qqlive";
                    info.downloadUrl = "http://imtt.dd.qq.com/16891/110A36BF492C6672528F40A4FFDB22B4.apk";
                }
                if (i == 2) {
                    info.packageName = "com.cleanmaster.mguard_cn";
                    info.downloadUrl = "http://imtt.dd.qq.com/16891/3CC768370B43EDF35F56BB3948C77BA8.apk";
                }
                dtos.add(info);
            }
            emitter.onNext(dtos);
        })
                .delay(1 * 1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apps -> {
                    progressbar.setVisibility(View.GONE);
                    adapter.initData(apps);

                    UpdateDAO.getAppsByStatus(realm, downloadedApps ->
                            Observable.create((ObservableEmitter<List<Integer>> emitter) -> {
                                List<Integer> positions = new ArrayList<>();
                                for (int i = 0; i < downloadedApps.size(); i++) {
                                    for (int x = 0; x < apps.size(); x++) {
                                        if (downloadedApps.get(i).id == apps.get(x).id) {
                                            apps.get(x).downloadStatus = AppUpdateInfoDTO.DOWNLOADED;
                                            apps.get(x).localPath = downloadedApps.get(i).localPath;
                                            positions.add(x);
                                        }
                                    }
                                }
                                emitter.onNext(positions);
                            })
                                    .flatMap(positions -> Observable.fromIterable(positions))
                                    .subscribe(position -> adapter.notifyItemChanged(position, R.id.tv_progress)));
                });
    }
}
