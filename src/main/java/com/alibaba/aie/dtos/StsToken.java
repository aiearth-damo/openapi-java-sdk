package com.alibaba.aie.dtos;

import java.util.Date;
import java.util.Map;

/**
 * @author : songci songci.sc@alibaba-inc.com
 * @created : 2024/3/18
 **/
public class StsToken {

    private String accessKeyId;
    private String accessKeySecret;
    private String bucket;
    private Date expiration;
    private String fileKey;
    private String fileName;
    private String region;
    private String securityToken;
    private String endpoint;

    public StsToken() {
    }

    public StsToken(Map<String, String> jsonObject) {
        this.accessKeyId = jsonObject.get("accessKeyId");
        this.accessKeySecret = jsonObject.get("accessKeySecret");
        this.bucket = jsonObject.get("bucket");
        this.expiration = new Date(Long.parseLong(jsonObject.get("expiration")));
        this.fileKey = jsonObject.get("fileKey");
        this.fileName = jsonObject.get("fileName");
        this.region = jsonObject.get("region");
        this.securityToken = jsonObject.get("securityToken");
        this.endpoint = jsonObject.get("endpoint");
    }

    public String getEndpoint() {
        String regionId = System.getenv("ALIYUN_REGION_ID");
        if (regionId == null) {
            return this.endpoint;
        }

        String epFirstPart = this.endpoint.split("\\.")[0];
        if (regionId.equals(epFirstPart)) {
            String epFirstPartInternal = epFirstPart + "-internal";
            return this.endpoint.replace(epFirstPart, epFirstPartInternal);
        } else {
            return this.endpoint;
        }
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
