package com.sescity.vbase.utils.oauth;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpClientUtil {
  private static HttpClientUtil instance;
  protected Charset charset;

  private HttpClientUtil() {}

  public static HttpClientUtil getInstance() {
    return getInstance(Charset.defaultCharset());
  }

  public static HttpClientUtil getInstance(Charset charset) {
    if (instance == null) {
      instance = new HttpClientUtil();
    }

    instance.setCharset(charset);
    return instance;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  public String doPost(String url) throws Exception {
    return this.doPost(url, (Map) null, (Map) null);
  }

  public String doPost(String url, Map<String, Object> params) throws Exception {
    return this.doPost(url, params, (Map) null);
  }

  public String doPost(String url, Map<String, Object> params, Map<String, String> header)
      throws Exception {
    String body = null;

    try {
      HttpPost httpPost = new HttpPost(url.trim());
      httpPost.setEntity(
          new UrlEncodedFormEntity(this.map2NameValuePairList(params), this.charset));
      if (header != null && !header.isEmpty()) {
        Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();

        while (it.hasNext()) {
          Map.Entry<String, String> entry = (Map.Entry) it.next();
          httpPost.setHeader(new BasicHeader((String) entry.getKey(), (String) entry.getValue()));
        }
      }

      body = this.execute(httpPost);
    } catch (Exception var8) {
      throw var8;
    }

    return body;
  }

  public String doPostJson(String url, Map<String, Object> params) throws Exception {
    return this.doPostJson(url, (Map) params, (Map) null);
  }

  public String doPostJson(String url, Map<String, Object> params, Map<String, String> header)
      throws Exception {
    String json = null;
    if (params != null && !params.isEmpty()) {
      Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();

      while (it.hasNext()) {
        Map.Entry<String, Object> entry = (Map.Entry) it.next();
        Object object = entry.getValue();
        if (object == null) {
          it.remove();
        }
      }

      json = JSON.toJSONString(params);
    }

    return this.postJson(url, json, header);
  }

  public String doPostJson(String url, String json) throws Exception {
    return this.doPostJson(url, (String) json, (Map) null);
  }

  public String doPostJson(String url, String json, Map<String, String> header) throws Exception {
    return this.postJson(url, json, header);
  }

  private String postJson(String url, String json, Map<String, String> header) throws Exception {
    String body = null;

    try {
      HttpPost httpPost = new HttpPost(url.trim());
      httpPost.setEntity(
          new StringEntity(json, ContentType.DEFAULT_TEXT.withCharset(this.charset)));
      httpPost.setHeader(new BasicHeader("Content-Type", "application/json"));
      if (header != null && !header.isEmpty()) {
        Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();

        while (it.hasNext()) {
          Map.Entry<String, String> entry = (Map.Entry) it.next();
          httpPost.setHeader(new BasicHeader((String) entry.getKey(), (String) entry.getValue()));
        }
      }

      body = this.execute(httpPost);
    } catch (Exception var8) {
      throw var8;
    }

    return body;
  }

  public String doGet(String url) throws Exception {
    return this.doGet(url, (Map) null, (Map) null);
  }

  public String doGet(String url, Map<String, String> header) throws Exception {
    return this.doGet(url, (Map) null, header);
  }

  public String doGet(String url, Map<String, Object> params, Map<String, String> header)
      throws Exception {
    String body = null;

    try {
      HttpGet httpGet = new HttpGet(url.trim());
      if (params != null && !params.isEmpty()) {
        String str =
            EntityUtils.toString(
                new UrlEncodedFormEntity(this.map2NameValuePairList(params), this.charset));
        String uri = httpGet.getURI().toString();
        if (uri.indexOf("?") >= 0) {
          httpGet.setURI(new URI(httpGet.getURI().toString() + "&" + str));
        } else {
          httpGet.setURI(new URI(httpGet.getURI().toString() + "?" + str));
        }
      }

      if (header != null && !header.isEmpty()) {
        Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();

        while (it.hasNext()) {
          Map.Entry<String, String> entry = (Map.Entry) it.next();
          httpGet.setHeader(new BasicHeader((String) entry.getKey(), (String) entry.getValue()));
        }
      }

      try (SSLClient sslClient = new SSLClient()) {
        System.out.println("Call Url:" + url);
        org.apache.http.HttpResponse response = sslClient.execute(httpGet); // 响应结果
        body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

    return body;
  }

  private String execute(HttpRequestBase requestBase) throws Exception {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    String body = null;

    try {
      CloseableHttpResponse response = httpclient.execute(requestBase);

      try {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          body = EntityUtils.toString(entity, this.charset.toString());
        }

        EntityUtils.consume(entity);
      } catch (Exception var16) {
        throw var16;
      } finally {
        response.close();
      }
    } catch (Exception var18) {
      throw var18;
    } finally {
      httpclient.close();
    }

    return body;
  }

  private List<NameValuePair> map2NameValuePairList(Map<String, Object> params) {
    if (params != null && !params.isEmpty()) {
      List<NameValuePair> list = new ArrayList();
      Iterator<String> it = params.keySet().iterator();

      while (it.hasNext()) {
        String key = (String) it.next();
        if (params.get(key) != null) {
          String value = String.valueOf(params.get(key));
          list.add(new BasicNameValuePair(key, value));
        }
      }

      return list;
    } else {
      return null;
    }
  }
}
