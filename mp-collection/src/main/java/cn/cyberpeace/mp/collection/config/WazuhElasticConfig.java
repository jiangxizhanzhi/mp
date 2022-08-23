package cn.cyberpeace.mp.collection.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class WazuhElasticConfig {

//    @Value("${mp.situationaware.wazuh.es-url}")
//    private String esUrl;
//
//
//    @Value("${mp.situationaware.wazuh.es-port}")
//    private Integer port;
//
//
//    @Value("${mp.situationaware.wazuh.es-username}")
//    private String userName;
//
//    @Value("${mp.situationaware.wazuh.es-password}")
//    private String passWord;


//    @Value("${mp.log.es-nodes}")
//    private String elasticsearchNodes;



    /**
     * es disable https client
     * @return RestHighLevelClient
     */
    /*@Bean(name="wazuhRestHighLevelClient")
    public RestHighLevelClient restHighLevelClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
            new UsernamePasswordCredentials(userName, passWord));

        var sslFactory = SSLFactory.builder()
            .withTrustingAllCertificatesWithoutValidation()
            .withHostnameVerifier((host, session) -> true).build();

        var restClientBuilder = RestClient
            .builder(new HttpHost(esUrl, port, "https"))
            .setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setSSLContext(sslFactory.getSslContext())
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE));


        return new RestHighLevelClient(restClientBuilder);
    }*/


//    @Bean(name="wazuhRestHighLevelClient")
//    public RestHighLevelClient restHighLevelClient() {
//        return new RestHighLevelClient(RestClient.builder(this.createHttpHost()));
//    }

    /**cn.cyberpeace.mp.situationaware
     * 创建 HttpHost 对象
     *
     * @return 返回 HttpHost 对象数组
     */
//    private HttpHost[] createHttpHost() {
//        Asserts.check(!ObjectUtils.isEmpty(elasticsearchNodes), "ElasticSearch cluster ip address cannot empty");
//        String[] ipArray = elasticsearchNodes.split(",");
//        var httpHosts = new HttpHost[ipArray.length];
//        for (int i = 0, len = ipArray.length; i < len; i++) {
//            var ipAddress = ipArray[i];
//            String[] values = ipAddress.split(":");
//            String ip = values[0];
//            var port = Integer.parseInt(values[1]);
//            httpHosts[i] = new HttpHost(ip, port, "http");
//        }
//        return httpHosts;
//    }

}
