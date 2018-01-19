package com.devin.app.store.index;

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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.DownloadApkUtils;
import com.devin.app.store.base.utils.DownloadUtils;
import com.devin.app.store.base.utils.MeasureUtils;
import com.devin.app.store.index.model.AppInfoDTO;
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

    private Context context;

    private List<AppInfoDTO> data = new ArrayList<>();

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

    public AppListAdapter(Context context) {
        this.context = context;
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
        holder.tv_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CLICK_POSITION = position;

                if (CommonUtils.isInstalled(context, model.packageName)) {
                    try {
                        CommonUtils.openApp(context, model.packageName);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                if (model.downloadStatus == AppInfoDTO.PREPARE_DOWNLOAD) {
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
                                                model.downloadStatus = AppInfoDTO.DOWNLOADING;
                                                notifyItemChanged(position, R.id.tv_progress);
                                            }
                                        });
                                    }
                                    if (bean.max == bean.progressLength) {
                                        BaseApp.mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                model.downloadStatus = AppInfoDTO.DOWNLOADED;
                                                model.localPath = bean.path;
                                                model.downloadProgress = 100;
                                                holder.layout_progressbar.setVisibility(View.GONE);
                                                context.startActivity(DownloadApkUtils.getIntent(bean.path));
                                                notifyItemChanged(position);
                                            }
                                        });

                                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                AppInfoDTO app = realm.createObject(AppInfoDTO.class, model.id);
                                                app.localPath = bean.path;
                                                app.downloadStatus = AppInfoDTO.DOWNLOADED;
                                                app.appSize = bean.max;
                                            }
                                        });
                                    }
                                }
                            })
                            .download(model.downloadUrl);
                } else if (model.downloadStatus == AppInfoDTO.DOWNLOADED) {
                    if (!TextUtils.isEmpty(model.localPath)) {
                        context.startActivity(DownloadApkUtils.getIntent(model.localPath));
                    }
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
                if (holder.layout_progressbar.getVisibility() != View.VISIBLE) {
                    holder.layout_progressbar.setVisibility(View.VISIBLE);
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
            tv_progress = itemView.findViewById(R.id.tv_progress);
        }
    }
}