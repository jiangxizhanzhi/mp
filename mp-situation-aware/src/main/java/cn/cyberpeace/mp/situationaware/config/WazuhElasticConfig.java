package cn.cyberpeace.mp.situationaware.config;

import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class WazuhElasticConfig {

    @Value("${mp.situationaware.wazuh.es-url}")
    private String esUrl;


    @Value("${mp.situationaware.wazuh.es-port}")
    private Integer port;


    @Value("${mp.situationaware.wazuh.es-username}")
    private String userName;

    @Value("${mp.situationaware.wazuh.es-password}")
    private String passWord;



    /**
     * es disable https client
     * @return RestHighLevelClient
     */
    @Bean(name="wazuhRestHighLevelClient")
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
    }


}
