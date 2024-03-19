package com.alibaba.aie;

import com.alibaba.aie.dtos.DataType;
import com.alibaba.aie.dtos.StsToken;
import com.alibaba.aie.params.AiesegJobType;
import com.alibaba.aie.params.CreateAiesegJobRequest;
import com.alibaba.aie.params.PublishLocalImgIgeRequest;
import com.alibaba.aie.params.PublishLocalImgRequest;
import com.alibaba.aie.params.PublishLocalShapefileRequest;
import com.alibaba.aie.params.PublishLocalTiffRequest;
import com.alibaba.aie.params.PublishLocalTiffRpbRequest;
import com.alibaba.aie.params.PublishLocalTiffRpcRequest;
import com.alibaba.aie.params.PublishLocalTiffTfwRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.aiearth_engine20220609.Client;
import com.aliyun.aiearth_engine20220609.models.CreateAIJobResponse;
import com.aliyun.aiearth_engine20220609.models.CreateAIJobResponseBody;
import com.aliyun.aiearth_engine20220609.models.PublishRasterRequest;
import com.aliyun.aiearth_engine20220609.models.PublishRasterResponse;
import com.aliyun.aiearth_engine20220609.models.PublishVectorRequest;
import com.aliyun.aiearth_engine20220609.models.PublishVectorResponse;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.teaopenapi.models.Config;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.aie.ApiHelper.getApiHost;
import static com.alibaba.aie.ApiHelper.getHost;

public class ExtClient extends Client {

    private final Logger logger = LoggerFactory.getLogger(ExtClient.class);


    public ExtClient(Config config) throws Exception {
        super(config);
    }

    public ExtClient(String aliyunAkId, String aliyunAkSecret) throws Exception {
        super(Config.build(new HashMap<String, String>() {{
            put("accessKeyId", aliyunAkId);
            put("accessKeySecret", aliyunAkSecret);
            put("regionId", "cn-hangzhou");
            put("endpoint", getApiHost());
        }}));
    }

    /**
     * 提交AIESEG任务
     *
     * @param request
     * @return
     * @throws Exception
     */
    public CreateAIJobResponse createAiesegJob(CreateAiesegJobRequest request) throws Exception {
        request.validate();
        String url = getHost() + "/mariana/openapi/job/ai/submit";

        Map<String, Object> src = null;
        if (request.getInput() instanceof CreateAiesegJobRequest.RasterParam) {
            src = new HashMap<>();
            src.put("dataId", ((CreateAiesegJobRequest.RasterParam) request.getInput()).getDataId());
            src.put("type", "raster");
            src.put("band_no", ((CreateAiesegJobRequest.RasterParam) request.getInput()).getBandName());
        } else if (request.getInput() instanceof CreateAiesegJobRequest.MapServiceParam) {
            src = new HashMap<>();
            src.put("dataId", ((CreateAiesegJobRequest.MapServiceParam) request.getInput()).getDataId());
            src.put("type", "map_service");
            src.put("mapServiceZoomLevel", ((CreateAiesegJobRequest.MapServiceParam) request.getInput()).getZoomLevel());
        } else {
            throw new Exception("Request Input 不正确");
        }

        if (request.getAiesegJobType() == AiesegJobType.AIE_SEG_PROMPT && request.getPixelThreshold() == null) {
            request.setPixelThreshold(50);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("project_id", request.getProjectId());
        data.put("job_name", request.getJobName());
        data.put("app", request.getAiesegJobType().getCode());
        Map<String, Object> finalSrc = src;
        data.put("tiff_ids", Collections.singletonList(new HashMap<String, Object>() {{
            put("idx", 1);
            put("src", finalSrc);
        }}));
        data.put("ratio", request.getConfidence());
        data.put("area_ratio", request.getPixelThreshold());
        data.put("referShapeDataId", request.getFilterShapeDataId());
        data.put("refer_shape_wkt", request.getFilterShapeWkt());
        data.put("extras", new HashMap<String, Object>() {{
            put("mask_prompt_id", request.getVisualPromptId());
        }});

        if (request.getTextPrompt() != null) {
            ((Map<String, Object>) data.get("extras")).put("text_prompt", new HashMap<String, Object>() {{
                put("text_prompt_list", request.getTextPrompt());
            }});
        }

        logger.info("提交AIESEG任务 url: " + url + ", body: " + data);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(JSON.toJSONString(data), StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("x-aie-auth-token", ApiHelper.getToken(this).getToken());

            HttpResponse resp = httpClient.execute(httpPost);
            String body = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
            if (resp.getStatusLine().getStatusCode() != 200) {
                throw new Exception("提交AIESEG任务服务出错: " + resp.getStatusLine().getStatusCode() + " " + body);
            } else {
                JSONObject respObject = JSON.parseObject(body);
                Boolean success = respObject.getBoolean("success");
                if (!success) {
                    throw new Exception("提交AIESEG任务服务出错: " + body);
                }

                JSONObject module = respObject.getJSONObject("module");
                String app = module.getString("app");

                JSONArray successList = module.getJSONArray("success");
                JSONArray failedList = module.getJSONArray("failed");

                List<CreateAIJobResponseBody.CreateAIJobResponseBodyJobs> jobs = new ArrayList<>();
                for (int i = 0; i < successList.size(); i++) {
                    JSONObject element = successList.getJSONObject(i);
                    CreateAIJobResponseBody.CreateAIJobResponseBodyJobs sJobs = new CreateAIJobResponseBody.CreateAIJobResponseBodyJobs();
                    sJobs.setJobId(element.getLong("job_id"));
                    sJobs.setName(element.getString("job_name"));
                    sJobs.setSuccess(true);
                    jobs.add(sJobs);
                }
                for (int i = 0; i < failedList.size(); i++) {
                    JSONObject element = failedList.getJSONObject(i);
                    CreateAIJobResponseBody.CreateAIJobResponseBodyJobs sJobs = new CreateAIJobResponseBody.CreateAIJobResponseBodyJobs();
                    sJobs.setJobId(element.getLong("job_id"));
                    sJobs.setName(element.getString("job_name"));
                    sJobs.setSuccess(false);
                    jobs.add(sJobs);
                }

                CreateAIJobResponseBody respBody = new CreateAIJobResponseBody();
                respBody.setJobs(jobs);
                respBody.setApp(app);
                respBody.setProjectId(request.getProjectId());
                respBody.setRequestId(null);
                CreateAIJobResponse createAIJobResponse = new CreateAIJobResponse();
                createAIJobResponse.setBody(respBody);
                createAIJobResponse.setHeaders(new HashMap<>());
                createAIJobResponse.setStatusCode(200);

                return createAIJobResponse;
            }
        }


    }

    /**
     * 发布本地TIFF
     *
     * @param request
     * @return
     * @throws Throwable
     */
    public PublishRasterResponse publishLocalTiff(PublishLocalTiffRequest request) throws Throwable {

        String fileUri = uploadSingleFile(request.getLocalFilePath(), DataType.RASTER,
                "tif", request.getPartSize(), request.getTaskNum(), request.getProgressListener());

        PublishRasterRequest publishRasterRequest = new PublishRasterRequest();
        publishRasterRequest.setName(request.getName());
        publishRasterRequest.setAcquisitionDate(request.getAcquisitionDate());
        publishRasterRequest.setDownloadUrl(fileUri);
        publishRasterRequest.setFileType("tiff");

        return this.publishRaster(publishRasterRequest);
    }

    /**
     * 发布本地Img文件
     *
     * @param request
     * @return
     * @throws Throwable
     */
    public PublishRasterResponse publishLocalImg(PublishLocalImgRequest request) throws Throwable {
        String fileUri = uploadSingleFile(request.getLocalFilePath(), DataType.RASTER,
                "img", request.getPartSize(), request.getTaskNum(), request.getProgressListener());

        PublishRasterRequest publishRasterRequest = new PublishRasterRequest();
        publishRasterRequest.setName(request.getName());
        publishRasterRequest.setAcquisitionDate(request.getAcquisitionDate());
        publishRasterRequest.setDownloadUrl(fileUri);
        publishRasterRequest.setFileType("img");

        return this.publishRaster(publishRasterRequest);
    }

    /**
     * 发布本地Shapefile文件
     *
     * @param request
     * @return
     * @throws Throwable
     */
    public PublishVectorResponse publishLocalShapefile(PublishLocalShapefileRequest request) throws Throwable {
        String fileUri = uploadSingleFile(request.getLocalFilePath(), DataType.VECTOR,
                "zip", request.getPartSize(), request.getTaskNum(), request.getProgressListener());

        PublishVectorRequest publishVectorRequest = new PublishVectorRequest();
        publishVectorRequest.setName(request.getName());
        publishVectorRequest.setDownloadUrl(fileUri);

        return publishVector(publishVectorRequest);
    }

    /**
     * 发布本地ImgIge文件
     *
     * @param request
     * @return
     * @throws Throwable
     */
    public PublishRasterResponse publishLocalImgIge(PublishLocalImgIgeRequest request) throws Throwable {
        String[] fileUris = uploadDoubleFile(request.getMainFilePath(), request.getAttachFilePath(),
                DataType.RASTER, "img", "ige",
                request.getPartSize(), request.getTaskNum(), request.getProgressListener());
        PublishRasterRequest publishRasterRequest = new PublishRasterRequest();
        publishRasterRequest.setName(request.getName());
        publishRasterRequest.setAcquisitionDate(request.getAcquisitionDate());
        publishRasterRequest.setDownloadUrl(fileUris[0]);
        publishRasterRequest.setAttachDownloadUrl(fileUris[1]);
        publishRasterRequest.setFileType("img");
        publishRasterRequest.setAttachFileType("ige");
        return this.publishRaster(publishRasterRequest);
    }

    /**
     * 发布本地TiffRpc文件
     *
     * @param request
     * @return
     * @throws Throwable
     */
    public PublishRasterResponse publishLocalTiffRpc(PublishLocalTiffRpcRequest request) throws Throwable {
        String[] fileUris = uploadDoubleFile(request.getMainFilePath(), request.getAttachFilePath(),
                DataType.RASTER, "tif", "rpc",
                request.getPartSize(), request.getTaskNum(), request.getProgressListener());
        PublishRasterRequest publishRasterRequest = new PublishRasterRequest();
        publishRasterRequest.setName(request.getName());
        publishRasterRequest.setAcquisitionDate(request.getAcquisitionDate());
        publishRasterRequest.setDownloadUrl(fileUris[0]);
        publishRasterRequest.setAttachDownloadUrl(fileUris[1]);
        publishRasterRequest.setFileType("tiff");
        publishRasterRequest.setAttachFileType("rpc");
        return this.publishRaster(publishRasterRequest);
    }

    /**
     * 发布本地TiffRpb文件
     *
     * @param request
     * @return
     * @throws Throwable
     */
    public PublishRasterResponse publishLocalTiffRpb(PublishLocalTiffRpbRequest request) throws Throwable {
        String[] fileUris = uploadDoubleFile(request.getMainFilePath(), request.getAttachFilePath(),
                DataType.RASTER, "tif", "rpb",
                request.getPartSize(), request.getTaskNum(), request.getProgressListener());
        PublishRasterRequest publishRasterRequest = new PublishRasterRequest();
        publishRasterRequest.setName(request.getName());
        publishRasterRequest.setAcquisitionDate(request.getAcquisitionDate());
        publishRasterRequest.setDownloadUrl(fileUris[0]);
        publishRasterRequest.setAttachDownloadUrl(fileUris[1]);
        publishRasterRequest.setFileType("tiff");
        publishRasterRequest.setAttachFileType("rpb");
        return this.publishRaster(publishRasterRequest);
    }

    /**
     * 发布本地TiffTfw文件
     *
     * @param request
     * @return
     * @throws Throwable
     */
    public PublishRasterResponse publishLocalTiffTfw(PublishLocalTiffTfwRequest request) throws Throwable {
        String[] fileUris = uploadDoubleFile(request.getMainFilePath(), request.getAttachFilePath(),
                DataType.RASTER, "tif", "tfw",
                request.getPartSize(), request.getTaskNum(), request.getProgressListener());
        PublishRasterRequest publishRasterRequest = new PublishRasterRequest();
        publishRasterRequest.setName(request.getName());
        publishRasterRequest.setAcquisitionDate(request.getAcquisitionDate());
        publishRasterRequest.setDownloadUrl(fileUris[0]);
        publishRasterRequest.setAttachDownloadUrl(fileUris[1]);
        publishRasterRequest.setFileType("tiff");
        publishRasterRequest.setAttachFileType("tfw");
        return this.publishRaster(publishRasterRequest);
    }

    private String uploadSingleFile(String localFilePath,
                                    DataType dataType,
                                    String fileExt,
                                    Integer partSize,
                                    Integer taskNum,
                                    ProgressListener progressListener) throws Throwable {
        return doUpload(localFilePath, dataType, fileExt, partSize, taskNum, null, progressListener);
    }

    private String[] uploadDoubleFile(String mainFilePath,
                                      String attachFilePath,
                                      DataType dataType,
                                      String fileExt,
                                      String attachFileExt,
                                      Integer partSize,
                                      Integer taskNum,
                                      ProgressListener progressListener) throws Throwable {
        String mainFileUri = doUpload(mainFilePath, dataType, fileExt, partSize, taskNum, null, progressListener);
        String prevFileName = mainFilePath.substring(mainFilePath.lastIndexOf("/") + 1);

        String attachFileUri = doUpload(attachFilePath, dataType, attachFileExt, partSize, taskNum, prevFileName, progressListener);

        return new String[]{mainFileUri, attachFileUri};
    }

    private String doUpload(String localFilePath,
                            DataType dataType,
                            String fileExt,
                            Integer partSize,
                            Integer taskNum,
                            String prevFileName,
                            ProgressListener progressListener) throws Throwable {
        StsToken stsToken = ApiHelper.getStsToken(this, dataType, fileExt, prevFileName);

        OSS client = new OSSClientBuilder()
                .build(stsToken.getEndpoint(), stsToken.getAccessKeyId(), stsToken.getAccessKeySecret(), stsToken.getSecurityToken());
        UploadFileRequest uploadFileRequest = new UploadFileRequest(stsToken.getBucket(), stsToken.getFileKey(), localFilePath,
                partSize, taskNum);

        if (null != progressListener) {
            uploadFileRequest.withProgressListener(progressListener);
        }

        client.uploadFile(uploadFileRequest);

        return "https://" + stsToken.getBucket() + "." + stsToken.getEndpoint() + "/" + stsToken.getFileKey();
    }
}
