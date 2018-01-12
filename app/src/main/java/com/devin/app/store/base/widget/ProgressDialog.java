package com.devin.app.store.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.devin.app.store.R;


/**
 * Created by Devin on 17/4/19.
 */

public class ProgressDialog extends Dialog {

    public ProgressDialog(@NonNull Context context) {
        super(context, R.style.module_base_ProgressDialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    public void show() {
        if (!this.isShowing() && getContext() != null) {
            try {
                super.show();
            } catch (Exception e) {

            }
        }
    }

    public void dismiss() {
        if (this.isShowing()) {
            try {
                super.dismiss();
            }catch (Exception e){

            }
        }
    }

    public void initView() {
        getWindow().setContentView(R.layout.module_base_common_loading);
        // 获取屏幕宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        WindowManager.LayoutParams mLp = getWindow().getAttributes();
        mLp.gravity = Gravity.CENTER;
        mLp.alpha = 1.0f;
        mLp.width = width;
        getWindow().setAttributes(mLp);
    }

}
