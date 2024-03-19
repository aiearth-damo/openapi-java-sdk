package com.alibaba.aie.params;

import com.aliyun.aiearth_engine20220609.models.PublishRasterRequest;
import com.aliyun.oss.event.ProgressListener;

/**
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/18
 **/
public abstract class BasePublishLocalFileRequest extends PublishRasterRequest {

    /**
     * 上传到OSS的文件分片大小，默认4MB
     */
    private Integer partSize = 4 * 1024 * 1024;
    /**
     * 上传到OSS的文件的并发任务数，默认2
     */
    private Integer taskNum = 2;

    /**
     * OSS上传进度监听
     */
    private ProgressListener progressListener;

    @Override
    public PublishRasterRequest setDownloadUrl(String downloadUrl) {
        throw new UnsupportedOperationException("不支持使用下载地址发布");
    }

    @Override
    public PublishRasterRequest setAttachDownloadUrl(String attachDownloadUrl) {
        throw new UnsupportedOperationException("不支持使用下载地址发布");
    }

    public Integer getPartSize() {
        return partSize;
    }

    public void setPartSize(Integer partSize) {
        this.partSize = partSize;
    }

    public Integer getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(Integer taskNum) {
        this.taskNum = taskNum;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }
}
