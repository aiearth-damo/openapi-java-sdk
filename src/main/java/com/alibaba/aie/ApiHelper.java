package com.alibaba.aie;

import com.alibaba.aie.dtos.Token;
import com.aliyun.aiearth_engine20220609.Client;
import com.aliyun.aiearth_engine20220609.models.GetUserTokenRequest;
import com.aliyun.aiearth_engine20220609.models.GetUserTokenResponse;

public class ApiHelper {
    private static Token token;

    public static String getHost() {
        String HOST = System.getenv("SDK_CLIENT_HOST");
        return null == HOST ? "https://engine-aiearth.aliyun.com" : HOST;
    }

    public static String getApiHost() {
        String HOST = System.getenv("OPENAPI_ENDPOINT");
        return null == HOST ? "aiearth-engine.cn-hangzhou.aliyuncs.com" : HOST;
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
