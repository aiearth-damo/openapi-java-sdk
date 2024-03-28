package com.alibaba.aie;

import com.alibaba.aie.dtos.DataType;
import com.alibaba.aie.dtos.StsToken;
import com.alibaba.aie.dtos.Token;
import com.alibaba.aie.utils.ObjectUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.aiearth_engine20220609.Client;
import com.aliyun.aiearth_engine20220609.models.GetUserTokenRequest;
import com.aliyun.aiearth_engine20220609.models.GetUserTokenResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.nio.charset.StandardCharsets;

public class ApiHelper {
    private static Token token;

    public static String getHost() {

        return ObjectUtils.getFirstNonNull(() -> System.getenv("SDK_CLIENT_HOST"),
                () -> System.getProperty("SDK_CLIENT_HOST"),
                () -> "https://engine-aiearth.aliyun.com");
    }

    public static String getApiHost() {
        return ObjectUtils.getFirstNonNull(() -> System.getenv("OPENAPI_ENDPOINT"),
                () -> System.getProperty("OPENAPI_ENDPOINT"),
                () -> "aiearth-engine.cn-hangzhou.aliyuncs.com");
    }

    public static StsToken getStsToken(Client client, DataType dataType, String fileExt, String prevFileName) throws Exception {

        URIBuilder uriBuilder = new URIBuilder(getHost() + "/mariana/openapi/oss/getStsToken")
                .addParameter("client", "aiearthopenapi")
                .addParameter("data_type", dataType.name())
                .addParameter("file_ext", fileExt);
        if(null != prevFileName) {
            uriBuilder.addParameter("prev_file_name", prevFileName);
        }

        HttpResponse resp = Request.Get(uriBuilder.build())
                .addHeader("Content-Type", "application/json")
                .addHeader("x-aie-auth-token", getToken(client).getToken())
                .execute()
                .returnResponse();

        // parse content to StsToken and return if resp status code is 200
        if (resp.getStatusLine().getStatusCode() == 200) {
            // parse content to JSONObject
            JSONObject jsonObject = JSON.parseObject(IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8));
            if (!jsonObject.getBoolean("success")) {
                throw new RuntimeException(jsonObject.getString("message"));
            }
            return jsonObject.getJSONObject("module").toJavaObject(StsToken.class);
        } else {
            throw new RuntimeException("getStsToken failed, status code: " + resp.getStatusLine().getStatusCode());
        }
    }


    public static Token getToken(Client client) throws Exception {

        if (null != token && token.getExpiresAt() - System.currentTimeMillis() > 24 * 3600 * 1000) {
            return token;
        }

        GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest();
        getUserTokenRequest.forceCreate = false;
        GetUserTokenResponse resp = client.getUserToken(getUserTokenRequest);

        Token token = new Token(resp.getBody().getToken(), resp.getBody().getExpiredAt());

        if (token.getExpiresAt() - System.currentTimeMillis() < 24 * 3600. * 1000) {
            getUserTokenRequest = new GetUserTokenRequest();
            getUserTokenRequest.forceCreate = true;
            resp = client.getUserToken(getUserTokenRequest);

            token = new Token(resp.getBody().getToken(), resp.getBody().getExpiredAt());
        }
        ApiHelper.token = token;
        return token;
    }

}
