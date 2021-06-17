package cn.cyberpeace.mp.datasandtable.utils;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.datasandtable.constant.DataSandtableConstant;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import javax.net.ssl.SSLContext;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class WazuhHttpsUtils {


    public static CloseableHttpClient createIgnoreSSLHttpClient() {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain,
                    String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGetWithAuthToken(String url) throws IOException {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = createIgnoreSSLHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + DataSandtableConstant.wazuhToken.get("wazuhToken"));
            response = httpClient.execute(httpGet);
            StatusLine sl = response.getStatusLine();
            if (sl.getStatusCode() == 401) {
                DataSandtableConstant.wazuhToken.invalidate("wazuhToken");
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + DataSandtableConstant.wazuhToken.get("wazuhToken"));
                response = httpClient.execute(httpGet);
            }
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new MpRuntimeException("sendGetWithAuth 失败");
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String sendGetWithAuth(String url, String userName, String password) throws IOException {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new MpRuntimeException("sendGetWithAuth 失败");
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
}
