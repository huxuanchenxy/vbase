package com.sescity.vbase.utils.oauth;

import com.sescity.vbase.conf.OauthConfig;
import java.util.HashMap;
import java.util.Map;

public class OauthUtils {

    public static String doLogin(String url, LoginEntity user, OauthConfig oauthConfig) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("client_id", oauthConfig.getClientId());
        params.put("username", oauthConfig.getAdminUsername());
        params.put("password", oauthConfig.getAdminPassword());
        params.put("code", "111");

        try {
            return HttpClientUtil.getInstance().doGet(url + "oauth2/sescity/token", params, (Map) null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}