package cn.cyberpeace.mp.sceneplugincloud.core.common;

import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.base.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class HttpClient {

    public static final int DEFAULT_TIMEOUT = 30000;

    public static final String DEFAULT_CHARSET = "utf-8";

    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    private org.apache.http.client.HttpClient innerHttpClient;

    private String server;

    private Map<String, String> headers;

    private Integer timeout = DEFAULT_TIMEOUT;

    private String charset = DEFAULT_CHARSET;

    private String contentType = DEFAULT_CONTENT_TYPE;

    private String formatUrl(String url, HttpParam param) {
        String serverUrl = StringUtils.join(server, url);
        if (param == null) {
            return serverUrl;
        }

        return StringUtils.joinWith("?", serverUrl, param.toPathString());
    }

    private Map<String, String> mergeHeaders(Map<String, String> mergingHeaders) {
        if (headers != null && mergingHeaders == null) {
            return null;
        }
        Map<String, String> mergedHeaders = new HashMap<>();
        if (headers != null) {
            headers.forEach(mergedHeaders::put);
        }
        if (mergingHeaders != null) {
            mergingHeaders.forEach(mergedHeaders::put);
        }
        return mergedHeaders;
    }

    private <T extends HttpRequestBase> T genRequest(Class<T> requestClass, String url, HttpParam param, HttpData data,
                                                     Map<String, String> headers, Integer timeout) {
        var request = ClassUtils.createInstance(requestClass);
        request.setURI(URI.create(formatUrl(url, param)));
        request.setConfig(RequestConfig.custom().setSocketTimeout(timeout == null ? this.timeout : timeout).build());

        Map<String, String> mergedHeaders = mergeHeaders(headers);
        if (mergedHeaders != null) {
            request.setHeaders(mergedHeaders.entrySet().stream().map(v ->
                    new BasicHeader(v.getKey(), v.getValue())
            ).toArray(Header[]::new));
        }
        if (data != null && request instanceof HttpEntityEnclosingRequestBase) {
            var entity = new StringEntity(data.toContent(), charset);
            entity.setContentEncoding(charset);
            entity.setContentType(contentType);
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return request;
    }

    private <T extends HttpRequestBase> HttpResponse request(Class<T> requestClass, String url, HttpData data,
                                                             HttpParam param, Map<String, String> headers,
                                                             Integer timeout)
            throws IOException {
        var request = genRequest(requestClass, url, param, data, headers, timeout);
        return innerHttpClient.execute(request);
    }

    private <T extends HttpRequestBase> String successRequest(Class<T> requestClass, String url, HttpData data,
                                                              HttpParam param, Map<String, String> headers,
                                                              Integer timeout) {
        var request = genRequest(requestClass, url, param, data, headers, timeout);
        HttpResponse response;
        try {
            response = innerHttpClient.execute(request);
        } catch (IOException e) {
            log.error("Http {} exception - url: {}, header: {}, body: {}, error: {}", request.getMethod(),
                    request.getURI(), request.getAllHeaders(),
                    data == null ? StringUtils.EMPTY : data.toContent(), e);
            return null;
        }

        if (!isSuccessResponse(response)) {
            log.error("Http {} status error - url: {}, header: {}, body: {}, error: {}", request.getMethod(),
                    request.getURI(), request.getAllHeaders(),
                    data == null ? StringUtils.EMPTY : data.toContent(), response.getStatusLine().getReasonPhrase());
            return null;
        }

        try {
            return result(response);
        } catch (IOException e) {
            log.error("Http {} receive exception - url: {}, header: {}, body: {}, error: {}", request.getMethod(),
                    request.getURI(), request.getAllHeaders(), data == null ? StringUtils.EMPTY : data.toContent(), e);
            return null;
        }
    }

    private boolean isSuccessStatusCode(int statusCode) {
        return statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES;
    }

    private boolean isSuccessResponse(HttpResponse httpResponse) {
        return isSuccessStatusCode(httpResponse.getStatusLine().getStatusCode());
    }

    public HttpClient(String server) {
        this.server = server;
        innerHttpClient = HttpClients.custom().disableAutomaticRetries().build();
    }

    public HttpClient(String server, int timeout) {
        this(server);
        this.timeout = timeout;
    }

    public String result(HttpResponse httpResponse) throws IOException {
        return EntityUtils.toString(httpResponse.getEntity(), charset);
    }

    public Map<String, Object> resultMap(String result) {
        if (StringUtils.isNotBlank(result)) {
            return JsonUtils.parse(result, new TypeReference<Map<String, Object>>() {});
        } else {
            return null;
        }
    }

    public Map<String, Object> resultMap(HttpResponse httpResponse) throws IOException {
        String result = result(httpResponse);
        return resultMap(result);
    }

    public <T> T resultBean(String result) {
        if (StringUtils.isNotBlank(result)) {
            return JsonUtils.parse(result, new TypeReference<T>() {});
        } else {
            return null;
        }
    }

    public <T> T resultBean(HttpResponse httpResponse) throws IOException {
        String result = result(httpResponse);
        return resultBean(result);
    }

    public HttpResponse get(String url, HttpParam param, Map<String, String> headers, Integer timeout)
            throws IOException {
        return request(HttpGet.class, url, null, param, headers, timeout);
    }

    public HttpResponse post(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout)
            throws IOException {
        return request(HttpPost.class, url, data, param, headers, timeout);
    }

    public HttpResponse put(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout)
            throws IOException {
        return request(HttpPut.class, url, data, param, headers, timeout);
    }

    public HttpResponse patch(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout)
            throws IOException {
        return request(HttpPatch.class, url, data, param, headers, timeout);
    }

    public HttpResponse delete(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout)
            throws IOException {
        return request(HttpDeleteWithBody.class, url, data, param, headers, timeout);
    }

    public String successGet(String url, HttpParam param, Map<String, String> headers, Integer timeout) {
        return successRequest(HttpGet.class, url, null, param, headers, timeout);
    }

    public String successPost(String url, HttpData data, HttpParam param, Map<String, String> headers,
                              Integer timeout) {
        return successRequest(HttpPost.class, url, data, param, headers, timeout);
    }

    public String successPut(String url, HttpData data, HttpParam param, Map<String, String> headers,
                             Integer timeout) {
        return successRequest(HttpPut.class, url, data, param, headers, timeout);
    }

    public String successPatch(String url, HttpData data, HttpParam param, Map<String, String> headers,
                               Integer timeout) {
        return successRequest(HttpPatch.class, url, data, param, headers, timeout);
    }

    public String successDelete(String url, HttpData data, HttpParam param, Map<String, String> headers,
                                Integer timeout) {
        return successRequest(HttpDeleteWithBody.class, url, data, param, headers, timeout);
    }

}
