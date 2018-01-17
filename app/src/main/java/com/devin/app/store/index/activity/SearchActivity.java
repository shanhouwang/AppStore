package com.devin.app.store.index.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.devin.app.store.R;
import com.devin.app.store.base.BaseActivity;

/**
 * Created by Devin on 2018/1/17.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
