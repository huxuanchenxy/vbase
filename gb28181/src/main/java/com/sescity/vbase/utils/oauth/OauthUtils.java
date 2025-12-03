package com.sescity.vbase.utils.oauth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OauthUtils {
  public static String doLogin(String url, LoginEntity user) {
    HashMap<String, Object> params = new HashMap<>();
    params.put("grant_type", "password");
    params.put("client_id", "c45fd4df-c0b2-4bb9-8634-c69ab38b860d");
    params.put("username", user.getUserName());
    params.put("password", user.getPassword());
    params.put("code", user.getCode());

    try {
      return HttpClientUtil.getInstance().doGet(url + "oauth2/sescity/token", params, (Map) null);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
