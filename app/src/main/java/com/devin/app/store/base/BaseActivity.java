package com.devin.app.store.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.devin.app.store.base.utils.CommonUtils;
import com.devin.app.store.base.utils.LogUtils;
import com.devin.app.store.base.widget.ProgressDialog;

/**
 * Created by Devin on 2018/1/9.
 */
public class BaseActivity extends AppCompatActivity {

    protected Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }

    private ProgressDialog dialog;

    public void showProgress() {
        BaseApp.mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (dialog == null) {
                    dialog = new ProgressDialog(mActivity);
                }
                LogUtils.d(">>>>>" + CommonUtils.hideSoftKeyboard(mActivity));
                dialog.show();
            }
        });
    }

    public void hideProgress() {
        BaseApp.mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                }
            }
        });
    }
}
