package com.scienjus.callback;

import com.scienjus.bean.IllustImage;

/**
 * Created by xieenlong on 15/8/11.
 */
public interface DownloadCallback {

    void onFinished(IllustImage illust, byte[] file);
}