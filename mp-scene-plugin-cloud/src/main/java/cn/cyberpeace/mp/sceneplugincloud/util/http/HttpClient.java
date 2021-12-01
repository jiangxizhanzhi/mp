package cn.cyberpeace.mp.sceneplugincloud.util.http;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class HttpClient {

    public static final int DEFAULT_TIMEOUT = 30000;

    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    private boolean logRequest;

    private org.apache.http.client.HttpClient innerHttpClient;

    private String server;

    private Map<String, String> headers;

    private Integer timeout = DEFAULT_TIMEOUT;

    private Charset charset = StandardCharsets.UTF_8;

    private String contentType = DEFAULT_CONTENT_TYPE;

    private String formatUrl(String url, HttpParam param) {
        var serverUrl = StringUtils.join(server, url);
        if (param == null) {
            return serverUrl;
        }

        return StringUtils.join(serverUrl, param.toPathString());
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
            entity.setContentEncoding(charset.toString());
            entity.setContentType(contentType);
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return request;
    }

    private <T extends HttpRequestBase> HttpResult request(Class<T> requestClass, String url, HttpData data,
                                                                  HttpParam param, Map<String, String> headers,
                                                                  Integer timeout) {
        var request = genRequest(requestClass, url, param, data, headers, timeout);
        var result = new HttpResult();
        result.setRequest(request);
        HttpResponse response;
        if (logRequest) {
            log.info("Http {} - url: {}, header: {}, body: {}", request.getMethod(), request.getURI(),
                    request.getAllHeaders(), data == null ? StringUtils.EMPTY : data.toContent());
        }
        try {
            response = innerHttpClient.execute(request);
        } catch (IOException e) {
            log.error("Http {} exception - url: {}, header: {}, body: {}, error: {}", request.getMethod(),
                    request.getURI(), request.getAllHeaders(),
                    data == null ? StringUtils.EMPTY : data.toContent(), e);
            result.setException(e);
            return result;
        }
        result.setResponse(response);

        if (!result.isSuccess()) {
            log.error("Http {} status error - url: {}, header: {}, body: {}, http code: {}, error: {}", request.getMethod(),
                    request.getURI(), request.getAllHeaders(),
                    data == null ? StringUtils.EMPTY : data.toContent(), response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase());
        }

        try {
            result.loadResult(charset);
        } catch (IOException e) {
            log.error("Http {} receive exception - url: {}, header: {}, body: {}, error: {}", request.getMethod(),
                    request.getURI(), request.getAllHeaders(), data == null ? StringUtils.EMPTY : data.toContent(), e);
            result.setException(e);
        }

        return result;
    }

    public HttpClient(String server) {
        this.server = server;
        innerHttpClient = HttpClients.custom().disableAutomaticRetries().build();
    }

    public HttpClient(String server, int timeout) {
        this(server);
        this.timeout = timeout;
    }

    public HttpResult get(String url, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpGet.class, url, null, param, headers, timeout);
    }

    public HttpResult post(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpPost.class, url, data, param, headers, timeout);
    }

    public HttpResult put(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpPut.class, url, data, param, headers, timeout);
    }

    public HttpResult patch(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpPatch.class, url, data, param, headers, timeout);
    }

    public HttpResult delete(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpDeleteWithBody.class, url, data, param, headers, timeout);
    }
}
