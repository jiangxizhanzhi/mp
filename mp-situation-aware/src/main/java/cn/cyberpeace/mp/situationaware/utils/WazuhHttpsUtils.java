package cn.cyberpeace.mp.situationaware.utils;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareErrorCode;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.google.common.util.concurrent.UncheckedExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
public class WazuhHttpsUtils {
    public static CloseableHttpClient createIgnoreSSLHttpClient() {
        try {
            var sslContext = new SSLContextBuilder().loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true).build();
            var sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        } catch (Exception e) {
            log.error("{}", e);
            throw new MpRuntimeException("createIgnoreSSLHttpClient error");
        }
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGetWithAuthToken(String url, List<NameValuePair> params) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = createIgnoreSSLHttpClient();

            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setParameters(params);
            HttpGet httpGet = new HttpGet(uriBuilder.build());

            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SituationalAwareConstant.wazuhToken.get("wazuhToken"));
            response = httpClient.execute(httpGet);
            StatusLine sl = response.getStatusLine();
            if (sl.getStatusCode() == 401) {
                SituationalAwareConstant.wazuhToken.invalidate("wazuhToken");
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SituationalAwareConstant.wazuhToken.get("wazuhToken"));
                response = httpClient.execute(httpGet);
            }
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (UncheckedExecutionException | IOException e) {
            log.error("connected exception: {}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.CONNECTION_FAIL);
        } catch (Exception e) {
            log.error("wazuh api error: {}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException("wazuh api query error!");
        }
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGetWithAuthToken(String url) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = createIgnoreSSLHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SituationalAwareConstant.wazuhToken.get("wazuhToken"));
            response = httpClient.execute(httpGet);
            StatusLine sl = response.getStatusLine();
            if (sl.getStatusCode() == 401) {
                SituationalAwareConstant.wazuhToken.invalidate("wazuhToken");
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + SituationalAwareConstant.wazuhToken.get("wazuhToken"));
                response = httpClient.execute(httpGet);
            }
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (UncheckedExecutionException | IOException e) {
            log.error("connected exception: {}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.CONNECTION_FAIL);
        } catch (Exception e) {
//            processException(e);
            log.error("wazuh api error: {}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException("wazuh api query error!");
        }
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGetWithAuth(String url, String userName, String password) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            var pass = userName + ":" + password;
            String encoding = Base64.getEncoder().encodeToString(pass.getBytes(StandardCharsets.UTF_8));
            httpClient = createIgnoreSSLHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
            response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (UncheckedExecutionException | IOException e) {
            log.error("connected exception: {}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException(SituationalAwareErrorCode.CONNECTION_FAIL);
        } catch (Exception e) {
//            processException(e);
            log.error("wazuh api error: {}", ExceptionUtil.stacktraceToString(e));
            throw new MpRuntimeException("wazuh api query error!");
        }
    }

    private static String doGetRequest(String url, HttpClient client) throws IOException {
        HttpGet httpget = new HttpGet(url);
        HttpResponse resp = client.execute(httpget);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpget.setConfig(requestConfig);
        // 获得返回值
        return EntityUtils.toString(resp.getEntity(), "UTF-8");
    }

//    private static void processException(Exception e) {
//        if (e instanceof IOException) {
//            log.error("connected exception: {}", ExceptionUtil.stacktraceToString(e));
//            throw new MpRuntimeException(SituationalAwareErrorCode.CONNECTION_FAIL);
//        }
//
//        if (e instanceof UncheckedExecutionException) {
//            log.error("unchecked exception: {}", ExceptionUtil.stacktraceToString(e));
//            throw new MpRuntimeException(SituationalAwareErrorCode.WAZUH_AUTHENTICATION_FAIL);
//        }
//    }
}
