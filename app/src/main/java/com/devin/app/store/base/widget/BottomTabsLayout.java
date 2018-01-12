package com.devin.app.store.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devin on 17/3/1.
 */
public class BottomTabsLayout extends LinearLayout {

    private List<BottomTabButton> bts = new ArrayList<>();
    private OnBottomTabClickListener mOnClickListener;

    public BottomTabsLayout(Context context) {
        super(context);
        clean();
    }

    public BottomTabsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        clean();
    }

    public BottomTabsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        clean();
    }

    public OnBottomTabClickListener getOnBottomTabClickListener() {
        return mOnClickListener;
    }

    public void setOnBottomTabClickListener(OnBottomTabClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
    }

    private void clean() {
        bts.clear();
    }

    public BottomTabsLayout add(String defUrl, String clickedUrl) {
        BottomTabButton bt = new BottomTabButton(getContext());
        LayoutParams params = new LayoutParams(-2, -2);
        bt.setLayoutParams(params);
        bt.set(new BottomTabButtonItem(defUrl, clickedUrl));
        bts.add(bt);
        return BottomTabsLayout.this;
    }

    public BottomTabsLayout add(String txt, int def, int clicked) {
        BottomTabButton bt = new BottomTabButton(getContext());
        LayoutParams params = new LayoutParams(-2, -1);
        bt.setLayoutParams(params);
        bt.set(new BottomTabButtonItem(txt, def, clicked));
        bts.add(bt);
        return BottomTabsLayout.this;
    }

    public void build() {
        for (int i = 0; i < bts.size(); i++) {
            BottomTabButton tab = bts.get(i);
            LayoutParams params = (LayoutParams) tab.getLayoutParams();
            params.weight = bts.size();
            tab.set(i, BottomTabsLayout.this);
            final int position = i;
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(position);
                    }
                    setChecked(position, true);
                }
            });
            addView(tab);
        }
    }

    public void setText(String text, int position) {
        View v = getChildAt(position);
        if (v != null
                && v instanceof BottomTabButton) {
            BottomTabButton b = (BottomTabButton) v;
            b.txt.setText(text);
        }
    }

    public BottomTabButton get(int position) {
        return bts.get(position);
    }

    public void setChecked(int position, boolean checked) {
        if (!checkPosition(position)) {
            return;
        }
        for (int i = 0; i < bts.size(); i++) {
            bts.get(i).setChecked(position == i ? checked : !checked);
        }
    }

    /**
     * 显示消息
     */
    public void show(int position, String msg) {
        if (!checkPosition(position)) {
            return;
        }
        bts.get(position).showMsg(msg);
    }

    /**
     * 隐藏消息
     */
    public void hide(int position) {
        if (!checkPosition(position)) {
            return;
        }
        bts.get(position).hideMsg();
    }

    /**
     * 检查是否是合理的位置
     */
    private boolean checkPosition(int position) {
        if (position + 1 > bts.size()) {
            return false;
        }
        if (bts.get(position) == null) {
            return false;
        }
        return true;
    }

}
