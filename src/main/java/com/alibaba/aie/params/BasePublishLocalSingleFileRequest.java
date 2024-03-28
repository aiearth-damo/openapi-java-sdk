package com.alibaba.aie.params;

import com.aliyun.oss.event.ProgressListener;

/**
 * 发布单文件影像的请求
 *
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/18
 **/
public abstract class BasePublishLocalSingleFileRequest extends BasePublishLocalFileRequest {

    /**
     * 本地文件路径
     */
    private String localFilePath;

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    @Override
    public ProgressListener getProgressListener() {
        return new UploadProgressListener(this.localFilePath);
    }
}
