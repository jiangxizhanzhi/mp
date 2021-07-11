package cn.cyberpeace.mp.log.config;

import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;


@Configuration(value = "LogElasticsearchConfig")
public class ElasticsearchConfig {

    @Autowired
    private LogConfig logConfig;

    @Bean(name="logRestHighLevelClient")
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(this.createHttpHost()));
    }

    
    private HttpHost[] createHttpHost() {
        Asserts.check(!ObjectUtils.isEmpty(logConfig.getEsNodes()), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = logConfig.getEsNodes().split(",");
        var httpHosts = new HttpHost[ipArray.length];
        for (int i = 0, len = ipArray.length; i < len; i++) {
            var ipAddress = ipArray[i];
            String[] values = ipAddress.split(":");
            String ip = values[0];
            var port = Integer.parseInt(values[1]);
            httpHosts[i] = new HttpHost(ip, port, "http");
        }
        return httpHosts;
    }
}
