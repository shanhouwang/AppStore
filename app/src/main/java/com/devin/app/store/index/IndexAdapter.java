package com.devin.app.store.index;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseApp;
import com.devin.app.store.base.utils.DownloadApkUtils;
import com.devin.app.store.base.utils.DownloadUtils;
import com.devin.app.store.base.utils.LogUtils;
import com.devin.app.store.index.model.AppModel;
import com.devin.tool_aop.annotation.CatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedOutputStream;

/**
 * Created by Devin on 2018/1/9.
 * <p>
 * 推荐页面
 */
public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.ViewHolder> {

    private Context context;

    private List<AppModel> data = new ArrayList<>();

    public void bindData(List<AppModel> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public IndexAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.index_fragment_item, null));
    }

    @Override
    @CatchException
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AppModel model = data.get(position);
        holder.tv_app_name.setText(model.appName);
        holder.rating_bar.setRating(model.rating);
        holder.tv_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.downloadStatus == AppModel.PREPARE_DOWNLOAD) {
                    model.downloadStatus = AppModel.DOWNLOADING;
                    holder.layout_progressbar.setVisibility(View.VISIBLE);
                    final TextView tv = addProgressTextView(holder.layout_progressbar, position);
                    model.downloadStatus = AppModel.DOWNLOADED;
                    DownloadApkUtils
                            .get((Activity) context, new DownloadUtils.DownloadCallBack() {
                                @Override
                                public void onResponse(final DownloadUtils.CallBackBean bean) {
                                    if (bean.max > bean.progressLength) {
                                        BaseApp.mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                int percent = (int) ((double) bean.progressLength / bean.max * 100);
                                                tv.setText(percent + "%");
                                            }
                                        });
                                    }
                                    if (bean.max == bean.progressLength) {
                                        BaseApp.mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                model.downloadStatus = AppModel.DOWNLOADED;
                                                model.localPath = bean.path;
                                                tv.setText("100%");
                                                holder.layout_progressbar.setVisibility(View.GONE);
                                                context.startActivity(DownloadApkUtils.getIntent(bean.path));
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            })
                            .download(model.downloadUrl);
                } else if (model.downloadStatus == AppModel.DOWNLOADED) {
                    if (!TextUtils.isEmpty(model.localPath)) {
                        context.startActivity(DownloadApkUtils.getIntent(model.localPath));
                    }
                }
            }
        });
        holder.tv_size.setText(model.appSize + "M");
        holder.tv_app_desc.setText(model.appDesc);
        setDownloadStatus(model.downloadStatus, holder, position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private TextView addProgressTextView(LinearLayout layout, int position) {
        View v = LayoutInflater.from(context).inflate(R.layout.index_fragment_item_element, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
        v.setLayoutParams(params);
        layout.addView(v);
        layout.setTag(new DownloadTagModel(v, position));
        return v.findViewById(R.id.tv_progress);
    }

    private void setDownloadStatus(int downloadStatus, ViewHolder holder, int currentPosition) {
        switch (downloadStatus) {
            case AppModel.PREPARE_DOWNLOAD:
                holder.tv_install.setText("下载");
                holder.layout_progressbar.setVisibility(View.GONE);
                holder.layout_install.setBackground(context.getDrawable(R.drawable.index_item_install_bg));
                holder.tv_install.setTextColor(context.getResources().getColor(R.color._4dbe2e));
                break;
            case AppModel.DOWNLOADING:
                holder.layout_progressbar.setVisibility(View.VISIBLE);
                break;
            case AppModel.DOWNLOADED:
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

    class DownloadTagModel {

        public DownloadTagModel(View v, int position) {
            this.v = v;
            this.position = position;
        }

        View v;
        int position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_app_cover;
        TextView tv_app_name;
        RatingBar rating_bar;
        TextView tv_classify_name;
        TextView tv_size;
        FrameLayout layout_install;
        LinearLayout layout_progressbar;
        TextView tv_install;
        TextView tv_app_desc;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_app_cover = itemView.findViewById(R.id.iv_app_cover);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
            rating_bar = itemView.findViewById(R.id.rating_bar);
            tv_classify_name = itemView.findViewById(R.id.tv_classify_name);
            tv_size = itemView.findViewById(R.id.tv_size);
            tv_install = itemView.findViewById(R.id.tv_install);
            tv_app_desc = itemView.findViewById(R.id.tv_app_desc);
            layout_install = itemView.findViewById(R.id.layout_install);
            layout_progressbar = itemView.findViewById(R.id.layout_progressbar);
        }
    }
}
