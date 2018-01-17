package com.devin.app.store.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.tool_aop.annotation.CatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devin on 2018/1/9.
 * <p>
 * 推荐页面
 */
public class SearchRecommAdapter extends RecyclerView.Adapter<SearchRecommAdapter.ViewHolder> {

    private Context context;

    private List<String> data = new ArrayList<>();

    public void initData(List<String> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void bindLoadMoreData(List<String> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public SearchRecommAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.index_fragment_item, null));
    }

    @Override
    @CatchException
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String model = data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_progress;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_progress = itemView.findViewById(R.id.tv_progress);
        }
    }
}