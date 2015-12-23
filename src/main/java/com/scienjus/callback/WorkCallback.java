package com.scienjus.callback;

import com.scienjus.model.Work;

/**
 * 发现符合条件的作品的回调
 * @author Scienjus
 * @date 2015/12/15.
 */
public interface WorkCallback {

    void onFound(Work work);
}
