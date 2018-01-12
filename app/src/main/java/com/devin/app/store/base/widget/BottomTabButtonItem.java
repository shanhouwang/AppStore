package com.devin.app.store.base.widget;

/**
 * Created by Devin on 17/3/1.
 */
public class BottomTabButtonItem {

    public boolean isResource;
    public String txt;
    public int def;
    public int clicked;
    public String defUrl;
    public String clickedUrl;

    public BottomTabButtonItem(String txt, int def, int clicked) {
        this.txt = txt;
        this.def = def;
        this.clicked = clicked;
        isResource = true;
    }

    public BottomTabButtonItem(String defUrl, String clickedUrl) {
        this.defUrl = defUrl;
        this.clickedUrl = clickedUrl;
        isResource = false;
    }
}