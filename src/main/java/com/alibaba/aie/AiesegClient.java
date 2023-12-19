package com.alibaba.aie;

import com.alibaba.aie.params.AiesegJobType;
import com.alibaba.aie.params.CreateAiesegJobRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.aiearth_engine20220609.Client;
import com.aliyun.aiearth_engine20220609.models.CreateAIJobResponse;
import com.aliyun.aiearth_engine20220609.models.CreateAIJobResponseBody;
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

public class AiesegClient extends Client {

    private final Logger logger = LoggerFactory.getLogger(AiesegClient.class);



    public AiesegClient(Config config) throws Exception {
        super(config);
    }

    public AiesegClient(String aliyunAkId, String aliyunAkSecret) throws Exception {
        super(Config.build(new HashMap<String, String>(){{
            put("accessKeyId", aliyunAkId);
            put("accessKeySecret", aliyunAkSecret);
            put("regionId", "cn-hangzhou");
            put("endpoint", getApiHost());
        }}));
    }

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

        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
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

}
