package com.alibaba.aie.params;

import com.aliyun.oss.event.ProgressListener;

/**
 * 双文件发布的基础请求
 *
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/18
 **/
public abstract class BasePublishLocalDoubleFileRequest extends BasePublishLocalFileRequest {

    /**
     * 主文件路径, 主要是img， tiff文件
     */
    private String mainFilePath;
    /**
     * 附件文件路径，主要是 ige, rpb, rpv/_rpc.txt, tfw文件
     */
    private String attachFilePath;

    public String getMainFilePath() {
        return mainFilePath;
    }

    public void setMainFilePath(String mainFilePath) {
        this.mainFilePath = mainFilePath;
    }

    public String getAttachFilePath() {
        return attachFilePath;
    }

    public void setAttachFilePath(String attachFilePath) {
        this.attachFilePath = attachFilePath;
    }

    @Override
    public ProgressListener getProgressListener() {
        return new UploadProgressListener(this.mainFilePath.substring(0, this.mainFilePath.lastIndexOf(".")));
    }
}
