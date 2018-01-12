package com.devin.app.store.base.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devin.app.store.R;
import com.devin.app.store.base.utils.MeasureUtils;

/**
 * Created by Devin on 17/3/1.
 */
public class BottomTabButton extends RelativeLayout {

    private BottomTabsLayout mBottomTabsLayout;
    private int position;

    private LinearLayout container;
    private ImageView iv;
    public TextView txt;
    private TextView tvMsg;

    private BottomTabButtonItem item;

    public BottomTabButton(Context context) {
        super(context);
        init();
    }

    public BottomTabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomTabButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        container = new LinearLayout(getContext());
        container.setId(R.id.module_base_container);
        LayoutParams rl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_IN_PARENT);
        container.setLayoutParams(rl);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        this.addView(container);
    }

    public void set(int position, BottomTabsLayout layout) {
        this.position = position;
        mBottomTabsLayout = layout;
    }

    public void set(BottomTabButtonItem item) {
        this.item = item;
        create();
    }

    public BottomTabButtonItem get() {
        return item;
    }

    public void create() {
        iv = new ImageView(getContext());
        iv.setScaleType(ImageView.ScaleType.CENTER);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(-2, -2);
        iv.setLayoutParams(ivParams);
        iv.setId(R.id.module_base_iv);
        if (item.isResource) {
            iv.setImageResource(item.def);
        } else {
            Glide.with(getContext()).load(item.defUrl).into(iv);
        }
        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OnBottomTabClickListener l = mBottomTabsLayout.getOnBottomTabClickListener();
                if (l != null) {
                    l.onClick(position);
                }
                mBottomTabsLayout.setChecked(position, true);
            }
        });
        this.container.addView(iv);

        // 字
        txt = new TextView(getContext());
        txt.setTextSize(10);
        txt.setTextColor(getContext().getResources().getColor(R.color.module_base_333333));
        txt.setText(item.txt);
        LinearLayout.LayoutParams txt_ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        txt_ll.topMargin = MeasureUtils.dp2px(2);
        txt.setLayoutParams(txt_ll);
        this.container.addView(txt);

        tvMsg = new TextView(getContext());
        tvMsg.setBackgroundResource(R.mipmap.module_base_ic_red);
        tvMsg.setLines(1);
        tvMsg.setTextSize(7);
        tvMsg.setTextColor(getResources().getColor(R.color.module_base_ffffff));
        tvMsg.setGravity(Gravity.CENTER);
        int dip14 = MeasureUtils.dp2px(14);
        LayoutParams tvParams = new LayoutParams(dip14, dip14);
        tvParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.module_base_container);
        tvParams.addRule(RelativeLayout.ALIGN_TOP, R.id.module_base_container);
        tvParams.rightMargin = MeasureUtils.dp2px(-5);
        tvParams.topMargin = MeasureUtils.dp2px(-2);
        tvMsg.setLayoutParams(tvParams);
        tvMsg.setVisibility(View.INVISIBLE);
        tvMsg.setEllipsize(TextUtils.TruncateAt.END);
        addView(tvMsg);
    }

    public void setChecked(boolean checked) {
        if (item.isResource) {
            iv.setImageResource(checked ? item.clicked : item.def);
        } else {
            Glide.with(getContext()).load(checked ? item.clickedUrl : item.defUrl).into(iv);
        }
    }

    public void showMsg(String count) {
        tvMsg.setText(count);
        tvMsg.setVisibility(View.VISIBLE);
    }

    public void hideMsg() {
        tvMsg.setVisibility(View.INVISIBLE);
    }

}
