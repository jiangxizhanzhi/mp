package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
            HttpEntity entity;
            if (data.multipart()) {
                var multipart = data.toMultipart();
                var multipartEntity = MultipartEntityBuilder.create();
                multipart.forEach((fieldName, fieldValue) -> {
                    if (fieldValue instanceof String) {
                        multipartEntity.addTextBody(fieldName, (String) fieldValue);
                    } else if (fieldValue instanceof File) {
                        multipartEntity.addBinaryBody(fieldName, (File) fieldValue);
                    } else if (fieldValue instanceof InputStream) {
                        multipartEntity.addBinaryBody(fieldName, (InputStream) fieldValue);
                    } else if (fieldValue instanceof byte[]) {
                        multipartEntity.addBinaryBody(fieldName, (byte[]) fieldValue);
                    } else if (fieldValue instanceof ContentBody) {
                        multipartEntity.addPart(fieldName, (ContentBody) fieldValue);
                    }
                });
                entity = multipartEntity.build();
            } else {
                var stringEntity = new StringEntity(data.toContent(), charset);
                stringEntity.setContentEncoding(charset.toString());
                stringEntity.setContentType(contentType);
                entity = stringEntity;
            }
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return request;
    }

    private <T extends HttpRequestBase> HttpResult request(Class<T> requestClass, String url, HttpData data,
                                                           HttpParam param, Map<String, String> headers,
                                                           Integer timeout, boolean loadResult) {
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

        if (loadResult) {
            try {
                result.loadResult(charset);
            } catch (IOException e) {
                log.error("Http {} receive exception - url: {}, header: {}, body: {}, error: {}", request.getMethod(),
                        request.getURI(), request.getAllHeaders(), data == null ? StringUtils.EMPTY : data.toContent(), e);
                result.setException(e);
            }
        }

        return result;
    }

    public static class HttpClientBuilder {
        private String server;
        private Integer timeout;
        private Integer maxConnPerRoute;
        private Integer maxConnTotal;
        private String username;
        private String password;

        public HttpClientBuilder configServer(String server) {
            this.server = server;
            return this;
        }

        public HttpClientBuilder configTimeout(Integer timeout) {
            this.timeout = timeout;
            return this;
        }

        public HttpClientBuilder configMaxConnPerRoute(Integer maxConnPerRoute) {
            this.maxConnPerRoute = maxConnPerRoute;
            return this;
        }

        public HttpClientBuilder configMaxConnTotal(Integer maxConnTotal) {
            this.maxConnTotal = maxConnTotal;
            return this;
        }

        public HttpClientBuilder configBasicAuth(String username, String password) {
            this.username = username;
            this.password = password;
            return this;
        }

        public HttpClient build() {
            return new HttpClient(server, timeout, maxConnPerRoute, maxConnTotal, username, password);
        }
    }

    private HttpClient(String server, Integer timeout, Integer maxConnPerRoute, Integer maxConnTotal,
                      String username, String password) {
        this.server = server;
        var clientBuilder = HttpClients.custom().disableAutomaticRetries();
        if (maxConnPerRoute != null) {
            clientBuilder.setMaxConnPerRoute(maxConnPerRoute);
        }
        if (maxConnTotal != null) {
            clientBuilder.setMaxConnTotal(maxConnTotal);
        }
        if (username != null && password != null) {
            var credentialsProvider = new BasicCredentialsProvider();
            var usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
            credentialsProvider.setCredentials(AuthScope.ANY, usernamePasswordCredentials);
            clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }
        innerHttpClient = clientBuilder.build();
        if (timeout != null) {
            this.timeout = timeout;
        }
    }

    public static HttpClientBuilder builder() {
        return new HttpClientBuilder();
    }

    public HttpResult get(String url, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpGet.class, url, null, param, headers, timeout, true);
    }

    public HttpResult post(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpPost.class, url, data, param, headers, timeout, true);
    }

    public HttpResult put(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpPut.class, url, data, param, headers, timeout, true);
    }

    public HttpResult patch(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpPatch.class, url, data, param, headers, timeout, true);
    }

    public HttpResult delete(String url, HttpData data, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpDeleteWithBody.class, url, data, param, headers, timeout, true);
    }

    public HttpResult streamGet(String url, HttpParam param, Map<String, String> headers, Integer timeout) {
        return request(HttpGet.class, url, null, param, headers, timeout, false);
    }

    public HttpResult download(OutputStream os, String url, HttpParam param, Map<String, String> headers,
                               Integer timeout) {
        var httpResult = streamGet(url, param, headers, timeout);
        if (!httpResult.isSuccess()) {
            var msg = String.format("download http file %s status error: %s", url, httpResult.getResult());
            log.error(msg);
            httpResult.setException(new RuntimeException(msg));
            return httpResult;
        }

        var entity = httpResult.getResponse().getEntity();
        try {
            StreamUtils.copy(entity.getContent(), os);
        } catch (IOException e) {
            log.error("download http file {} get content error: {}", url, e);
            httpResult.setException(e);
            return httpResult;
        }

        return httpResult;
    }

}
