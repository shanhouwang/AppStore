package com.devin.app.store.index;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.utils.DownloadApkUtils;
import com.devin.app.store.base.utils.DownloadUtils;
import com.devin.app.store.base.utils.LogUtils;
import com.devin.app.store.index.model.AppInfoDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devin on 2018/1/9.
 * <p>
 * 推荐页面
 */
public class IndexBaseAdapter extends BaseAdapter {

    private static final String TAG = IndexBaseAdapter.class.getSimpleName();

    private Context context;

    private List<AppInfoDto> data = new ArrayList<>();

    public void initData(List<AppInfoDto> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void bindLoadMoreData(List<AppInfoDto> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public IndexBaseAdapter(Context context) {
        this.context = context;
    }

    private void setDownloadStatus(final AppInfoDto model, final ViewHolder holder) {
        switch (model.downloadStatus) {
            case AppInfoDto.PREPARE_DOWNLOAD:
                holder.tv_install.setText("下载");
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.layout_install.setBackground(context.getDrawable(R.drawable.index_item_install_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._4dbe2e));
                break;
            case AppInfoDto.DOWNLOADING:
                if (holder.layout_progressbar.getVisibility() != View.VISIBLE) {
                    holder.layout_progressbar.setVisibility(View.VISIBLE);
                }
                holder.tv_progress.setText(model.downloadProgress + "%");
                break;
            case AppInfoDto.DOWNLOADED:
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.tv_install.setText("安装");
                holder.layout_install.setBackground(context.getDrawable(R.drawable.index_item_downloaded_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._ffffff));
                break;
            default:
                holder.layout_install.setBackground(context.getDrawable(R.drawable.index_item_install_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._4dbe2e));
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.tv_install.setText("下载");
                break;
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.index_fragment_item, viewGroup, false);
            ViewHolder vh = new ViewHolder();
            vh.iv_app_cover = view.findViewById(R.id.iv_app_cover);
            vh.tv_app_name = view.findViewById(R.id.tv_app_name);
            vh.rating_bar = view.findViewById(R.id.rating_bar);
            vh.tv_classify_name = view.findViewById(R.id.tv_classify_name);
            vh.tv_size = view.findViewById(R.id.tv_size);
            vh.tv_install = view.findViewById(R.id.tv_install);
            vh.tv_app_desc = view.findViewById(R.id.tv_app_desc);
            vh.layout_install = view.findViewById(R.id.layout_install);
            vh.layout_progressbar = view.findViewById(R.id.layout_progressbar);
            vh.tv_progress = view.findViewById(R.id.tv_progress);
            view.setTag(vh);
        }
        final ViewHolder holder = (ViewHolder) view.getTag();
        final AppInfoDto model = data.get(position);
        holder.tv_app_name.setText(model.appName);
        holder.rating_bar.setRating(model.rating);
        holder.rating_bar.setClickable(false);
        holder.tv_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.downloadStatus == AppInfoDto.PREPARE_DOWNLOAD) {
                    model.downloadStatus = AppInfoDto.DOWNLOADING;
                    holder.layout_progressbar.setVisibility(View.VISIBLE);
                    DownloadApkUtils
                            .get((Activity) context, new DownloadUtils.DownloadCallBack() {
                                @Override
                                public void onResponse(final DownloadUtils.CallBackBean bean) {
                                    if (bean.max > bean.progressLength) {
                                        BaseApp.mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                int percent = (int) ((double) bean.progressLength / bean.max * 100);
                                                model.downloadProgress = percent;
                                                LogUtils.d(TAG, "percent: " + percent);
                                                holder.tv_progress.setText(percent + "%");
                                            }
                                        });
                                    }
                                    if (bean.max == bean.progressLength) {
                                        BaseApp.mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                model.downloadStatus = AppInfoDto.DOWNLOADED;
                                                model.localPath = bean.path;
                                                model.downloadProgress = 100;
                                                context.startActivity(DownloadApkUtils.getIntent(bean.path));
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            })
                            .download(model.downloadUrl);
                } else if (model.downloadStatus == AppInfoDto.DOWNLOADED) {
                    if (!TextUtils.isEmpty(model.localPath)) {
                        context.startActivity(DownloadApkUtils.getIntent(model.localPath));
                    }
                }
            }
        });
        holder.tv_size.setText(model.appSize + "M");
        holder.tv_app_desc.setText(model.appDesc);
        setDownloadStatus(model, holder);
        return view;
    }

    class ViewHolder {
        ImageView iv_app_cover;
        TextView tv_app_name;
        RatingBar rating_bar;
        TextView tv_classify_name;
        TextView tv_size;
        FrameLayout layout_install;
        LinearLayout layout_progressbar;
        TextView tv_install;
        TextView tv_app_desc;
        TextView tv_progress;
    }
}