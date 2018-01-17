package com.devin.app.store.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.base.utils.RealmUtils;
import com.devin.app.store.search.dao.SearchDAO;
import com.devin.app.store.search.model.RecommandModel;
import com.devin.app.store.search.model.SearchHistoryDTO;
import com.devin.tool_aop.annotation.CatchException;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Devin on 2018/1/9.
 * <p>
 * 推荐页面
 */
public class SearchRecommAdapter extends RecyclerView.Adapter<SearchRecommAdapter.ViewHolder> {

    private SearchActivity context;

    private List<RecommandModel> data = new ArrayList<>();

    public void initData(List<RecommandModel> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void bindLoadMoreData(List<RecommandModel> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public SearchRecommAdapter(SearchActivity context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_activity_recomm_item, parent, false));
    }

    @Override
    @CatchException
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final RecommandModel model = data.get(position);
        holder.tv_recommend.setText(model.keyword);
        if (model.formHistory) {
            holder.tv_recommend.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_search_history), null);
        } else {
            holder.tv_recommend.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.ic_search), null);
        }
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.changeAppListAdapter();
                if (null == SearchDAO.getByKeyWord(model.keyword)) {
                    RealmUtils.create(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            SearchHistoryDTO dto = realm.createObject(SearchHistoryDTO.class);
                            dto.keyWord = model.keyword;
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View v;

        TextView tv_recommend;

        public ViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            tv_recommend = itemView.findViewById(R.id.tv_recommend);
        }
    }
}