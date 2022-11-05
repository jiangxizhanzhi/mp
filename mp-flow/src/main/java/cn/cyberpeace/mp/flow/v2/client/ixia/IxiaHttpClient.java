package cn.cyberpeace.mp.flow.v2.client.ixia;

import cn.cyberpeace.mp.base.util.http.HttpClient;
import cn.cyberpeace.mp.base.util.http.HttpData;
import cn.cyberpeace.mp.base.util.http.HttpParam;
import cn.cyberpeace.mp.base.util.http.HttpResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class IxiaHttpClient {

    private final HttpClient httpClient;

    public IxiaHttpClient(String server, Integer timeout, Integer poolSize) {
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("SSL");
            X509TrustManager tm = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0,
                                               String arg1) throws CertificateException {
                }
            };
            ctx.init(null, new TrustManager[]{ tm }, null);
        } catch (Exception e) {

        }
        SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
        httpClient = HttpClient.builder()
                .configServer(server)
                .configTimeout(timeout)
                .configMaxConnTotal(poolSize)
                .configMaxConnPerRoute(poolSize)
                .config(ssf)
                .build();
        httpClient.setLogRequest(true);
    }

    public <T extends IxiaResponse> T ixiaPost(String url, HttpData data, HttpParam param, Class<T> tClass, TypeReference<T> valueTypeRef) {
        try {
            return ixiaResultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
            try {
                return tClass.getConstructor().newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public <T extends IxiaResponse> T ixiaPatch(String url, HttpData data, HttpParam param, Class<T> tClass, TypeReference<T> valueTypeRef) {
        try {
            return ixiaResultBean(httpClient.patch(url, data, param, this.getHeaders(), null), valueTypeRef, "patch", url, data, param);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
            try {
                return tClass.getConstructor().newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public <T extends IxiaResponse> T ixiaDelete(String url, HttpData data, HttpParam param, Class<T> tClass, TypeReference<T> valueTypeRef) {
        try {
            return ixiaResultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
            try {
                return tClass.getConstructor().newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }
    public <T extends IxiaResponse> T ixiaPut(String url, HttpData data, HttpParam param, Class<T> tClass, TypeReference<T> valueTypeRef) {
        try {
            return ixiaResultBean(httpClient.put(url, data, param, this.getHeaders(), null), valueTypeRef, "put", url, data, param);
        } catch (Exception e) {
            if (!"Entity may not be null".equals(e.getMessage())) {
                throw e;
            }
            try {
                return tClass.getConstructor().newInstance();
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public <T extends IxiaResponse> T post(String url, HttpData data, HttpParam param, Class<T> valueType, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueType, "post", url, data, param);
    }

    public <T extends IxiaResponse> T delete(String url, HttpData data, HttpParam param, Class<T> valueType) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueType, "delete", url, data, param);
    }

    public <T extends IxiaResponse> T post(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.post(url, data, param, this.getHeaders(), null), valueTypeRef, "post", url, data, param);
    }

    public <T extends IxiaResponse> T delete(String url, HttpData data, HttpParam param, TypeReference<T> valueTypeRef) {
        return resultBean(httpClient.delete(url, data, param, this.getHeaders(), null), valueTypeRef, "delete", url, data, param);
    }

    private <T extends IxiaResponse> T ixiaResultBean(HttpResult result, TypeReference<T> valueTypeRef,
                                                      String method, String url, HttpData data, HttpParam param) {
        var objectMapper = new ObjectMapper();
//        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            var response = objectMapper.readValue(result.getResult(), valueTypeRef);
            return response;
        } catch (Exception e) {
            log.error("Ixia {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return (T) new IxiaResponse();
        }
    }

    private <T> T resultBean(HttpResult result, Class<T> valueType, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Ixia {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            return null;
        }

        T response;
        var objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            response = objectMapper.readValue(result.getResult(), valueType);
        } catch (Exception e) {
            log.error("Ixia {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    private <T> T resultBean(HttpResult result, TypeReference<T> valueTypeRef, String method, String url, HttpData data,
                             HttpParam param) {
        if (StringUtils.isEmpty(result.getResult())) {
            log.error("Ixia {}[url - {}?{}, data - {}] empty result: ", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent());
            return null;
        }

        T response;
        var objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
        try {
            response = objectMapper.readValue(result.getResult(), valueTypeRef);
        } catch (Exception e) {
            log.error("Ixia {}[url - {}?{}, data - {}] parse result[{}] error: {}", method, url,
                    param == null ? StringUtils.EMPTY : param.toPathString(),
                    data == null ? StringUtils.EMPTY : data.toContent(), result, e);
            return null;
        }

        return response;
    }

    private Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-KEY", "17045ab29fba48c18808e1f95c4c496b");
//        headers.put("Host", "10.100.7.199");
//        headers.put("Sec-Fetch-Mode", "cors");
//        headers.put("Sec-Fetch-Site", "same-origin");
        return headers;
    }
}
