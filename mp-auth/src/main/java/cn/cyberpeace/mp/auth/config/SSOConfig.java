package cn.cyberpeace.mp.auth.config;

import lombok.Data;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

/**
 * @BelongsPackage: cn.cyberpeace.mp.auth.config
 * @Author: hhuang
 * @CreateTime: 2025/03/12  09:10
 * @Description: 单点登录核心类型
 * @Version: 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "mp.auth.sso")
public class SSOConfig {

    /**
     * 应用标识
     */
    private String appId;

    /**
     * 4a单点登录核心服务url
     */
    private String url;

    /**
     * 实际jar包中未使用此配置  login_4a_url = url.substring(0, url.lastIndexOf("/"));
     */
    private String login4aUrl;

    /**
     * 4a单点配置文件绝对路径
     */
    private String configFilePath;

    /**
     * 连接超时
     */
    private Integer connTimeout;

    /**
     * 读超时
     */
    private Integer readTimeout;

    /**
     * 首页url
     */
    private String homePageUrl;

    /**
     * 外部首页url
     */
    private String outHomePageUrl;

    /**
     * soc登录页url
     */
    private String socLoginUrl;

    /**
     * soc票据校验url
     */
    private String socTicketCheckUrl;

    /**
     * soc登出url
     */
    private String socLogoutUrl;

    /**
     * 默认重定向地址url
     */
    private String defaultRedirectUrl;


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        RestTemplate restTemplate = null;
        try {
            TrustStrategy acceptingTrustStrategy = (chain, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

            HttpClientBuilder clientBuilder = HttpClients.custom();

            CloseableHttpClient httpClient = clientBuilder.setSSLSocketFactory(sslsf).build();

            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);

            restTemplate = new RestTemplate(requestFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restTemplate;
    }
}
