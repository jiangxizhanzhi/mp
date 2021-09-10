package cn.cyberpeace.mp.base.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2021/9/8
 */
public final class HttpUtilsV2 {
    public static <T> T sendPost(String url, MpHttpArgs request, Class<T> resultType) {
        return doSend(url, HttpPost.class, request, resultType);
    }

    public static HttpResponse sendPost(String url, MpHttpArgs request) {
        return doSend(url, HttpPost.class, request);
    }

    public static <T> T sendGet(String url, MpHttpArgs request, Class<T> resultType) {
        return doSend(url, HttpGet.class, request, resultType);
    }

    public static <T> HttpResponse sendGet(String url, MpHttpArgs request) {
        return doSend(url, HttpGet.class, request);
    }

    public static <T> T sendPatch(String url, MpHttpArgs request, Class<T> resultType) {
        return doSend(url, HttpPatch.class, request, resultType);
    }

    public static HttpResponse sendPatch(String url, MpHttpArgs request) {
        return doSend(url, HttpPatch.class, request);
    }

    public static <T> T sendDelete(String url, MpHttpArgs request, Class<T> resultType) {
        return doSend(url, HttpDelete.class, request, resultType);
    }

    public static HttpResponse sendDelete(String url, MpHttpArgs request) {
        return doSend(url, HttpDelete.class, request);
    }

    private static <T extends HttpRequestBase, E> E doSend(String url, Class<T> methodType, MpHttpArgs request, Class<E> resultType) {
        HttpResponse response = doSend(url, methodType, request);
        HttpEntity responseEntity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(responseEntity);
        } catch (IOException e) {
            throw new RuntimeException("");
        }
        if (result == null) {
            throw new RuntimeException("");
        }
        if (resultType == String.class) {
            return (E) result;
        } else if (resultType == JSONObject.class) {
            return (E) JSONObject.parseObject(result);
        } else {
            return JSON.parseObject(result, resultType);
        }
    }

    private static <T extends HttpRequestBase> HttpResponse doSend(String url, Class<T> methodType, MpHttpArgs request) {
        if (request.getBodyContent() != null && HttpPost.class != methodType && HttpPatch.class != methodType) {
            throw new RuntimeException("unsupported http method");
        }
        try {
            T method = buildMethod(url, methodType, request);
            return HttpClientV2.instance().execute(method);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static <T extends HttpRequestBase> T buildMethod(String url, Class<T> methodType, MpHttpArgs request) throws Exception {
        T method = methodType.getConstructor(String.class).newInstance(url);
        if (method == null) {
            throw new RuntimeException();
        }
        if (request.getBodyContent() != null) {
            HttpEntityEnclosingRequestBase base = (HttpEntityEnclosingRequestBase) method;
            base.setEntity(new StringEntity(request.getBodyContent().getValue(), "utf8"));
        }
        if (request.getRequestConfig() != null) {
            method.setConfig(request.getRequestConfig());
        }
        Map<String, String> finalHeaders = getFinalHeaders(methodType, request.getHeaders());
        finalHeaders.forEach((name, value) -> method.addHeader(name, value));
        return method;
    }

    private static <T> Map<String, String> getFinalHeaders(Class<T> methodType, Map<String, String> input) {
        if (!input.isEmpty()) {
            return input;
        } else if (methodType == HttpPost.class) {
            return MpHttpHeaders.DEFAULT_POST_HEADERS.getValues();
        } else {
            return Collections.EMPTY_MAP;
        }
    }
}