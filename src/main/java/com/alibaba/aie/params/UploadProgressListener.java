package com.alibaba.aie.params;

import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/19
 **/
public class UploadProgressListener implements ProgressListener {
    private static final Logger logger = LoggerFactory.getLogger(UploadProgressListener.class);

    private long bytesWritten = 0;
    private long totalBytes = -1;
    private boolean succeed = false;

    private final String fileName;

    public UploadProgressListener(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSucceed() {
        return succeed;
    }

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                logger.info("Start to upload {}......", this.fileName);
                break;
            case REQUEST_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                logger.info("{} bytes in total will be uploaded to OSS", this.totalBytes);
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                this.bytesWritten += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
                    logger.info("{} {} bytes written, progress: {}%({}/{})",
                            fileName, bytes, percent, this.bytesWritten, this.totalBytes);
                } else {
                    logger.info("{} {} bytes written, ratio: unknown" + "({}/...)",
                            fileName, bytes, this.bytesWritten);
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                logger.info("Succeed to upload {}, {} bytes have been transferred in total",
                        fileName, this.bytesWritten);
                break;
            case TRANSFER_FAILED_EVENT:
                logger.info("Failed to upload {}, {} bytes have been transferred",
                        fileName, this.bytesWritten);
                break;
            default:
                break;
        }
    }

}
