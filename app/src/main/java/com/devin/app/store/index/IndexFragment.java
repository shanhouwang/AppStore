package com.devin.app.store.index;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.devin.app.store.R;
import com.devin.app.store.base.event.DownloadCancleEvent;
import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.LogUtils;
import com.devin.app.store.base.utils.MeasureUtils;
import com.devin.app.store.base.utils.ThreadUtils;
import com.devin.app.store.index.dao.AppDAO;
import com.devin.app.store.index.model.AppInfoDTO;
import com.devin.app.store.search.SearchActivity;
import com.devin.refreshview.MarsRefreshView;
import com.devin.refreshview.MercuryOnLoadMoreListener;
import com.stx.xhb.xbanner.XBanner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 推荐页面
 * <p>
 *
 * @author Devin
 */
public class IndexFragment extends Fragment implements View.OnClickListener {

    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        EventBus.getDefault().unregister(this);
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
        mAppListAdapter.notifyItemChanged(AppListAdapter.CLICK_POSITION, R.id.tv_install);
    }

    @Subscribe
    public void onMessageEvent(DownloadCancleEvent event) {
        mAppListAdapter.notifyItemChanged(AppListAdapter.CLICK_POSITION, R.id.tv_install);
    }

    private MarsRefreshView mMarsRefreshView;
    private ProgressBar progressbar;
    private AppListAdapter mAppListAdapter;
    private View mHeaderView;
    private XBanner banner;
    private LinearLayout searchLayout;
    private View searchLine;

    private void initView(View v) {
        v.findViewById(R.id.layout_search).setOnClickListener(this);
        searchLine = v.findViewById(R.id.search_line);
        mMarsRefreshView = v.findViewById(R.id.marsRefreshView);
        progressbar = v.findViewById(R.id.progressbar);
        mAppListAdapter = new AppListAdapter(getContext(), realm);
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.index_fragment_banner, null);
        banner = mHeaderView.findViewById(R.id.banner);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, MeasureUtils.dp2px(200));
        banner.setLayoutParams(params);

        mMarsRefreshView
                .setLinearLayoutManager()
                .setAdapter(mAppListAdapter)
                .addHeaderView(mHeaderView)
                .setMercuryOnLoadMoreListener(1, page -> {
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
                });
        mMarsRefreshView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                searchLayoutAnim();
            }
        });
        List<Integer> imgUrls = new ArrayList<>();
        imgUrls.add(R.mipmap.banner_1);
        imgUrls.add(R.mipmap.banner_2);
        imgUrls.add(R.mipmap.banner_3);
        banner.setData(imgUrls, null);
        banner.setmAdapter((banner, model, view, position) -> {
            ImageView iv = (ImageView) view;
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setImageResource((Integer) model);
        });
        initData();
    }

    private void initData() {
        progressbar.setVisibility(View.VISIBLE);
        ThreadUtils.get(ThreadUtils.Type.SCHEDULED).callBack(obj -> {
            progressbar.setVisibility(View.GONE);
            mAppListAdapter.initData((List<AppInfoDTO>) obj);
            updateStatus((List<AppInfoDTO>) obj);
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
        AppDAO.getDownloadedApps(realm, (RealmResults<AppInfoDTO> appsOfDb) -> {
            Observable.create((ObservableEmitter<List<Integer>> emitter) -> {
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
                emitter.onNext(notifyPositions);
            })
                    .flatMap((List<Integer> positions) -> Observable.fromIterable(positions))
                    .subscribe(position -> mAppListAdapter.notifyItemChanged(position, R.id.tv_install));
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_search:
                Intent i = new Intent(getContext(), SearchActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private static float factor = MeasureUtils.dp2px(225);

    /**
     * 顶部滑动动画处理
     */
    private void searchLayoutAnim() {
        int y = getScrollYDistance();
        if (searchLayout == null) {
            searchLayout = getActivity().findViewById(R.id.search_layout);
        }
        if (y < factor) {
            if (searchLine.getVisibility() == View.VISIBLE) {
                searchLine.setVisibility(View.INVISIBLE);
            }
            float percent = y / factor;
            searchLayout.getBackground().mutate().setAlpha((int) (255 * percent));
        } else {
            if (searchLine.getVisibility() == View.INVISIBLE) {
                searchLine.setVisibility(View.VISIBLE);
            }
            searchLayout.getBackground().mutate().setAlpha(255);
        }
    }

    public int getScrollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mMarsRefreshView.getRecyclerView().getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }
}
