package com.devin.app.store.classify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devin.app.store.R;
import com.devin.app.store.classify.model.ClassifyModel;
import com.devin.app.store.index.AppListActivity;
import com.devin.tool_aop.annotation.CatchException;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类页面Adapter
 * <p>
 *
 * @author Devin
 */
public class ClassifyAdapter extends RecyclerView.Adapter<ClassifyAdapter.ViewHolder> {

    private static final int SECOND_AMOUNT = 6;

    private static final int DIVISOR = 2;

    private Context context;

    private List<ClassifyModel> data = new ArrayList<>();

    public void initData(List<ClassifyModel> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public ClassifyAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.classify_fragment_item, parent, false));
    }

    @Override
    @CatchException
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ClassifyModel model = data.get(position);
        holder.init();
        holder.set(model.secondClassify);
        holder.tv_classify_name.setText(model.name);
        holder.tv_classify_name.setCompoundDrawablesWithIntrinsicBounds(null, context.getDrawable(model.imgUrl), null, null);
        holder.tv_classify_name.setOnClickListener(v -> {
            Intent i = new Intent(context, AppListActivity.class);
            i.putExtra(AppListActivity.KEY_TITLE, model.name);
            context.startActivity(i);
        });

        if (position % DIVISOR == 0) {
            holder.v.setBackgroundColor(context.getResources().getColor(R.color._ffffff));
        } else {
            holder.v.setBackgroundColor(context.getResources().getColor(R.color._e8ebed));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View v;

        TextView tv_classify_name;
        TextView tv_second_classify_1;
        TextView tv_second_classify_2;
        TextView tv_second_classify_3;
        TextView tv_second_classify_4;
        TextView tv_second_classify_5;
        TextView tv_second_classify_6;

        List<TextView> seconds;

        public ViewHolder(View itemView) {
            super(itemView);
            v = itemView;
            tv_classify_name = itemView.findViewById(R.id.tv_classify_name);
            seconds = new ArrayList<>();
            tv_second_classify_1 = itemView.findViewById(R.id.tv_second_classify_1);
            tv_second_classify_2 = itemView.findViewById(R.id.tv_second_classify_2);
            tv_second_classify_3 = itemView.findViewById(R.id.tv_second_classify_3);
            tv_second_classify_4 = itemView.findViewById(R.id.tv_second_classify_4);
            tv_second_classify_5 = itemView.findViewById(R.id.tv_second_classify_5);
            tv_second_classify_6 = itemView.findViewById(R.id.tv_second_classify_6);
            seconds.add(tv_second_classify_1);
            seconds.add(tv_second_classify_2);
            seconds.add(tv_second_classify_3);
            seconds.add(tv_second_classify_4);
            seconds.add(tv_second_classify_5);
            seconds.add(tv_second_classify_6);
        }

        public void init() {
            for (int i = 0; i < seconds.size(); i++) {
                seconds.get(i).setVisibility(View.INVISIBLE);
            }
        }

        public void set(List<ClassifyModel> models) {

            if (models == null) {
                return;
            }

            int length;

            if (models.size() > SECOND_AMOUNT) {
                length = SECOND_AMOUNT;
            } else {
                length = models.size();
            }

            for (int i = 0; i < length; i++) {
                seconds.get(i).setVisibility(View.VISIBLE);
                seconds.get(i).setText(models.get(i).name);
            }
        }
    }
}