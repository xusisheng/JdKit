package cn.banto.jd.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * http请求助手
 * @author BANTO
 */
public class  HttpRequest {

    /**
     * 默认UA
     */
    private final static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

    private Logger logger = LoggerFactory.getLogger(HttpRequest.class);
    private CloseableHttpClient client;
    private CookieStore cookieStore;
    private String userAgent;

    public HttpRequest(){
        cookieStore =  new BasicCookieStore();
        client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        setUserAgent(DEFAULT_USER_AGENT);
    }

    /**
     * 设置请求ua
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * get请求
     * @param url
     * @return
     */
    public String get(String url){
        return get(url,null);
    }

    /**
     * post请求
     * @param url
     * @param data
     * @return
     */
    public String post(String url, Map<String, String> data){
        return post(url, data, null);
    }

    /**
     * 上传文件
     * @param url
     * @param params
     * @return
     */
    public String upload(String url, HashMap<String, Object> params){
        return upload(url, params, null);
    }

    /**
     * 下载文件
     * @param url
     * @return
     */
    public InputStream download(String url){
        return download(url, null);
    }

    /**
     * get请求
     * @param url
     * @param referer
     * @return
     */
    public String get(String url, String referer){
        try {
            logger.debug("请求地址: {}", url);
            HttpGet request = new HttpGet(url);
            request.addHeader("User-Agent", userAgent);
            if(referer != null && referer.length() > 0){
                request.addHeader("Referer", referer);
            }
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("请求状态码: {}", statusCode);
            if(statusCode == 200){
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            logger.error("发送get请求失败", e);
        }

        return "";
    }

    /**
     * post请求
     * @param url
     * @param data
     * @param referer
     * @return
     */
    public String post(String url, Map<String, String> data, String referer){
        try {
            logger.debug("请求地址:{}", url);
            HttpPost request = new HttpPost(url);
            request.setEntity(MapToEntity(data));
            request.addHeader("User-Agent", userAgent);
            if(referer != null && referer.length() > 0){
                request.addHeader("Referer", referer);
            }
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("请求状态码:{}", statusCode);
            if(statusCode == 200){
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            logger.error("发送post请求失败", e);
        }

        return "";
    }

    /**
     * 上传文件
     * 示例: params.put("file", new File("logo.png"))
     * @param url
     * @param params
     * @param referer
     * @return
     */
    public String upload(String url, Map<String, Object> params, String referer){
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            Iterator iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iterator.next();
                Object value = entry.getValue();
                if(value instanceof String){
                    builder.addPart(entry.getKey(), new StringBody(String.valueOf(value), ContentType.TEXT_PLAIN));
                } else if(value instanceof File){
                    builder.addPart(entry.getKey(), new FileBody((File) value));
                } else {
                    logger.warn("参数{}的数据类型{}不被支持!", entry.getKey(), entry.getValue().getClass().getName());
                }
            }

            HttpPost request = new HttpPost(url);
            request.addHeader("User-Agent", userAgent);
            if(referer != null && referer.length() > 0){
                request.addHeader("Referer", referer);
            }
            request.setEntity(builder.build());
            HttpResponse response = client.execute(request);

            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            logger.error("上传文件失败", e);
        }

        return "";
    }

    /**
     * 下载文件
     * @param url
     * @param referer
     * @return
     */
    public InputStream download(String url, String referer){
        try {
            logger.debug("请求地址: {}", url);
            HttpGet request = new HttpGet(url);
            request.addHeader("User-Agent", userAgent);
            if(referer != null && referer.length() > 0){
                request.addHeader("Referer", referer);
            }
            HttpResponse response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("请求状态码: {}", statusCode);
            if(statusCode == 200){
                return response.getEntity().getContent();
            }
        } catch (IOException e) {
            logger.error("下载文件失败", e);
        }

        return null;
    }

    /**
     * 获取cookie管理器
     * @return
     */
    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * 获取cookie
     * @param name
     * @return
     */
    public String getCookie(String name){
        List<Cookie> cookies = cookieStore.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * 将Map转为HttpEntity
     * @param parmas
     * @return
     */
    private HttpEntity MapToEntity(Map parmas){
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();

        Iterator iterator = parmas.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        try {
            return new UrlEncodedFormEntity(paramList, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
