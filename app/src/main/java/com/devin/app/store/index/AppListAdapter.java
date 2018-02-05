package com.devin.app.store.index;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.SPUtils;
import com.devin.app.store.index.dao.AppDAO;
import com.devin.app.store.index.model.AppInfoDTO;
import com.devin.downloader.MercuryDownloader;
import com.devin.tool_aop.annotation.CatchException;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * App列表适配器
 *
 * @author Devin
 */
public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

    private Activity context;

    private List<AppInfoDTO> data = new ArrayList<>();

    private SPUtils sp;

    public static int CLICK_POSITION;

    public void initData(List<AppInfoDTO> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void bindLoadMoreData(List<AppInfoDTO> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    private Realm realm;

    public AppListAdapter(Activity context, Realm realm) {
        this.context = context;
        this.realm = realm;
        sp = new SPUtils("download.sp");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.index_fragment_item, null));
    }

    @Override
    @CatchException
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AppInfoDTO model = data.get(position);
        holder.tv_app_name.setText(model.appName);
        holder.rating_bar.setRating(model.rating);
        holder.tv_install.setOnClickListener(v -> {

            CLICK_POSITION = position;

            if (CommonUtils.isInstalled(context, model.packageName)) {
                try {
                    CommonUtils.openApp(context, model.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (AppInfoDTO.PREPARE_DOWNLOAD == model.downloadStatus || AppInfoDTO.PAUSE_DOWNLOAD == model.downloadStatus) {
                model.downloadStatus = AppInfoDTO.DOWNLOADING;
                MercuryDownloader.build()
                        .url(model.downloadUrl)
                        .activity(context)
                        .setOnCancelListener(() -> {
                            model.downloadStatus = AppInfoDTO.PREPARE_DOWNLOAD;
                            notifyItemChanged(position, R.id.tv_progress);
                        })
                        .setOnProgressListener(bean -> BaseApp.mHandler.post(() -> {
                            int percent = (int) ((double) bean.progressLength / bean.contentLength * 100);
                            model.downloadProgress = percent;
                            notifyItemChanged(position, R.id.tv_progress);
                        }))
                        .setOnCompleteListener(bean -> BaseApp.mHandler.post(() -> {
                            model.localPath = bean.path;
                            model.downloadProgress = 100;
                            model.downloadStatus = AppInfoDTO.DOWNLOADED;
                            holder.layout_progressbar.setVisibility(View.GONE);
                            context.startActivity(CommonUtils.getIntent(bean.path));
                            notifyItemChanged(position);

                            realm.executeTransaction(realm -> {
                                AppInfoDTO app = AppDAO.getApp(realm, model.id);
                                if (null == app) {
                                    app = realm.createObject(AppInfoDTO.class, model.id);
                                }
                                app.localPath = bean.path;
                                app.downloadStatus = AppInfoDTO.DOWNLOADED;
                                app.appSize = bean.contentLength;
                            });
                        })).start();
            } else if (model.downloadStatus == AppInfoDTO.DOWNLOADING) {
                MercuryDownloader.pause(model.downloadUrl);
                model.downloadStatus = AppInfoDTO.PAUSE_DOWNLOAD;
                notifyItemChanged(position);
                realm.executeTransaction(realm -> {
                    AppInfoDTO dto = AppDAO.getApp(realm, model.id);
                    if (null == dto) {
                        dto = realm.createObject(AppInfoDTO.class, model.id);
                    }
                    dto.downloadStatus = AppInfoDTO.PAUSE_DOWNLOAD;
                    dto.appSize = model.downloadProgress;
                });
            } else if (model.downloadStatus == AppInfoDTO.DOWNLOADED) {
                if (!TextUtils.isEmpty(model.localPath)) {
                    context.startActivity(CommonUtils.getIntent(model.localPath));
                }
            }
        });
        holder.tv_size.setText(model.appSize + "M");
        holder.tv_app_desc.setText(model.appDesc);
        setStatus(model, holder);

        if (position % DIVISOR == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color._ffffff));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color._e8ebed));
        }
    }

    private static final int DIVISOR = 2;

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void setStatus(final AppInfoDTO model, final ViewHolder holder) {
        if (CommonUtils.isInstalled(context, model.packageName)) {
            holder.tv_install.setText("打开");
            holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_downloaded_bg));
            holder.tv_install.setTextColor(context.getResources().getColor(R.color._ffffff));
            return;
        }
        switch (model.downloadStatus) {
            case AppInfoDTO.PREPARE_DOWNLOAD:
                holder.tv_install.setText("下载");
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_install_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._4dbe2e));
                break;
            case AppInfoDTO.DOWNLOADING:
                if (!CommonUtils.checkVisible(holder.layout_progressbar)) {
                    holder.layout_progressbar.setVisibility(View.VISIBLE);
                }
                if (!CommonUtils.checkVisible(holder.progressbar)) {
                    holder.progressbar.setVisibility(View.VISIBLE);
                }
                if (CommonUtils.checkVisible(holder.progressbar)) {
                    holder.iv_pause.setVisibility(View.GONE);
                }
                holder.tv_progress.setText(model.downloadProgress + "%");
                break;
            case AppInfoDTO.PAUSE_DOWNLOAD:
                if (!CommonUtils.checkVisible(holder.layout_progressbar)) {
                    holder.layout_progressbar.setVisibility(View.VISIBLE);
                }
                if (!CommonUtils.checkVisible(holder.iv_pause)) {
                    holder.iv_pause.setVisibility(View.VISIBLE);
                }
                if (CommonUtils.checkVisible(holder.progressbar)) {
                    holder.progressbar.setVisibility(View.GONE);
                }
                holder.tv_progress.setText(model.downloadProgress + "%");
                break;
            case AppInfoDTO.DOWNLOADED:
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.tv_install.setText("安装");
                holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_downloaded_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._ffffff));
                break;
            default:
                holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_install_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._4dbe2e));
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.tv_install.setText("下载");
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        ImageView iv_app_cover;
        TextView tv_app_name;
        RatingBar rating_bar;
        TextView tv_classify_name;
        TextView tv_size;
        FrameLayout layout_install;
        LinearLayout layout_progressbar;
        ImageView iv_pause;
        ProgressBar progressbar;
        TextView tv_install;
        TextView tv_app_desc;
        TextView tv_progress;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_app_cover = itemView.findViewById(R.id.iv_app_cover);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            rating_bar = itemView.findViewById(R.id.rating_bar);
            tv_classify_name = itemView.findViewById(R.id.tv_classify_name);
            tv_size = itemView.findViewById(R.id.tv_size);
            tv_install = itemView.findViewById(R.id.tv_install);
            tv_app_desc = itemView.findViewById(R.id.tv_app_desc);
            layout_install = itemView.findViewById(R.id.layout_install);
            layout_progressbar = itemView.findViewById(R.id.layout_progressbar);
            iv_pause = itemView.findViewById(R.id.iv_pause);
            progressbar = itemView.findViewById(R.id.progressbar);
            tv_progress = itemView.findViewById(R.id.tv_progress);
        }
    }
}