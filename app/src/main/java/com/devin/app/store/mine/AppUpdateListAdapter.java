package com.devin.app.store.mine;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.SPUtils;
import com.devin.app.store.mine.model.AppUpdateInfoDTO;
import com.devin.downloader.CallBackBean;
import com.devin.downloader.MercuryDownloader;
import com.devin.downloader.OnDownloaderListener;
import com.devin.tool_aop.annotation.CatchException;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * App列表适配器
 *
 * @author Devin
 */
public class AppUpdateListAdapter extends RecyclerView.Adapter<AppUpdateListAdapter.ViewHolder> {

    private Context context;

    private List<AppUpdateInfoDTO> data = new ArrayList<>();

    private Realm realm;

    private SPUtils sp;

    public static int CLICK_POSITION;

    public void initData(List<AppUpdateInfoDTO> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void bindLoadMoreData(List<AppUpdateInfoDTO> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public AppUpdateListAdapter(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
        sp = new SPUtils("update.sp");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.app_update_item, null));
    }

    @Override
    @CatchException
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AppUpdateInfoDTO model = data.get(position);
        holder.tv_app_name.setText(model.appName);
        holder.tv_old_version.setText(model.oldVersion);
        holder.tv_new_version.setText(model.newVersion);
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
            if (model.downloadStatus == AppUpdateInfoDTO.PREPARE_DOWNLOAD) {
                model.downloadStatus = AppUpdateInfoDTO.DOWNLOADING;
                holder.layout_progressbar.setVisibility(View.VISIBLE);

                MercuryDownloader.url(model.downloadUrl)
                        .activity((Activity) context)
                        .setOnProgressListener(bean -> BaseApp.mHandler.post(() -> {
                            int percent = (int) ((double) bean.progressLength / bean.contentLength * 100);
                            model.downloadPercent = percent;
                            notifyItemChanged(position, R.id.tv_progress);
                        }))
                        .start(new OnDownloaderListener() {
                            @Override
                            public void onComplete(CallBackBean bean) {
                                BaseApp.mHandler.post(() -> {
                                    model.downloadStatus = AppUpdateInfoDTO.DOWNLOADED;
                                    model.localPath = bean.path;
                                    model.downloadPercent = 100;
                                    holder.layout_progressbar.setVisibility(View.GONE);
                                    context.startActivity(CommonUtils.getIntent(bean.path));
                                    notifyItemChanged(position);

                                    realm.executeTransaction(realm -> {
                                        AppUpdateInfoDTO newModel = realm.createObject(AppUpdateInfoDTO.class, model.id);
                                        newModel.downloadStatus = AppUpdateInfoDTO.DOWNLOADED;
                                        newModel.appSize = bean.contentLength;
                                        newModel.downloadProgress = bean.progressLength;
                                        newModel.localPath = bean.path;
                                    });
                                });
                            }

                            @Override
                            public void onError() {
                            }
                        });
            } else if (model.downloadStatus == AppUpdateInfoDTO.DOWNLOADED) {
                if (TextUtils.isDigitsOnly(model.localPath)) {
                    return;
                }
                if (!TextUtils.isEmpty(model.localPath)) {
                    context.startActivity(CommonUtils.getIntent(model.localPath));
                }
            }
        });
        holder.tv_app_size.setText(model.appSize + "M");
        holder.tv_app_time.setText(model.updateTime);
        holder.tv_app_update_desc.setText(model.updateDesc);
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

    private void setStatus(final AppUpdateInfoDTO model, final ViewHolder holder) {
        if (CommonUtils.isInstalled(context, model.packageName)) {
            holder.tv_install.setText("打开");
            holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_downloaded_bg));
            holder.tv_install.setTextColor(context.getResources().getColor(R.color._ffffff));
            return;
        }
        switch (model.downloadStatus) {
            case AppUpdateInfoDTO.PREPARE_DOWNLOAD:
                holder.tv_install.setText("更新");
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_install_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._4dbe2e));
                break;
            case AppUpdateInfoDTO.DOWNLOADING:
                if (holder.layout_progressbar.getVisibility() != View.VISIBLE) {
                    holder.layout_progressbar.setVisibility(View.VISIBLE);
                }
                holder.tv_progress.setText(model.downloadPercent + "%");
                break;
            case AppUpdateInfoDTO.DOWNLOADED:
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.tv_install.setText("安装");
                holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_downloaded_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._ffffff));
                break;
            default:
                holder.layout_install.setBackground(context.getResources().getDrawable(R.drawable.index_item_install_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._4dbe2e));
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.tv_install.setText("更新");
                break;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        ImageView iv_app_cover;
        TextView tv_app_name;
        TextView tv_old_version;
        TextView tv_new_version;
        TextView tv_app_size;
        TextView tv_app_time;
        TextView tv_app_update_desc;
        FrameLayout layout_install;
        LinearLayout layout_progressbar;
        TextView tv_install;
        TextView tv_progress;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_app_cover = itemView.findViewById(R.id.iv_app_cover);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            tv_old_version = itemView.findViewById(R.id.tv_old_version);
            tv_new_version = itemView.findViewById(R.id.tv_new_version);
            tv_app_size = itemView.findViewById(R.id.tv_app_size);
            tv_app_time = itemView.findViewById(R.id.tv_app_time);
            tv_app_update_desc = itemView.findViewById(R.id.tv_app_update_desc);
            tv_install = itemView.findViewById(R.id.tv_install);
            layout_install = itemView.findViewById(R.id.layout_install);
            layout_progressbar = itemView.findViewById(R.id.layout_progressbar);
            tv_progress = itemView.findViewById(R.id.tv_progress);
        }
    }
}