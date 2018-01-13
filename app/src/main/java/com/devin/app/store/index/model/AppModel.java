package com.devin.app.store.index.model;

/**
 * Created by Devin on 2018/1/11.
 */

public class AppModel {

    public int id;

    public String appName;

    /**
     * App分类
     */
    public String appClassify;
    public String appDesc;
    public int appSize;

    /**
     * 评分
     */
    public int rating;

    /**
     * App 下载的 Url
     */
    public String downloadUrl;

    /**
     * 下载状态
     *
     * 0：可以下载
     *
     * 1：正在下载
     *
     * 2：已经下载完成
     */
    public int downloadStatus;

    /**
     * 本地地址
     */
    public String localPath;

    /**
     *下载进度
     */
    public int downloadProgress;

    public static final int PREPARE_DOWNLOAD = 0;

    public static final int DOWNLOADING = 1;

    public static final int DOWNLOADED = 2;
}
