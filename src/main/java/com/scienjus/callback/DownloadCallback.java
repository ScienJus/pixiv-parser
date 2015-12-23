package com.scienjus.callback;

import com.scienjus.model.Work;

import java.util.List;

/**
 * 下载图片的回调
 * @author XieEnlong
 * @date 2015/12/16.
 */
public interface DownloadCallback {

    /**
     * 插画的回调（单张图）
     * @param work
     * @param file
     */
    void onIllustFinished(Work work, byte[] file);

    /**
     * 漫画的回调（多张图）
     * @param work
     * @param files
     */
    void onMangaFinished(Work work, List<byte[]> files);
}
