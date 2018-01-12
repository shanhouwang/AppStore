package com.devin.app.store.base.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.devin.app.store.R;

/**
 * Created by Devin on 2017/8/1.
 */

public class MyToast {

    private static View v;

    private MyToast() {
    }

    private static Toast getToast(Context context, String msg) {
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.module_base_toast, null);
        }
        TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
        tvMsg.setText(msg);
        Toast t = new Toast(context);
        t.setView(v);
        t.setDuration(Toast.LENGTH_SHORT);
        return t;
    }


    public static void show(Context context, String msg) {
        try {
            getToast(context, msg).show();
        }catch (Exception e){
        }
    }

}
