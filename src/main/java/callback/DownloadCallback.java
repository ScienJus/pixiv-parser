package callback;

import bean.IllustImage;

import java.awt.*;

/**
 * Created by xieenlong on 15/8/11.
 */
public interface DownloadCallback {

    void onFinished(IllustImage illust, byte[] file);
}
