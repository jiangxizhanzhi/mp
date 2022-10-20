package cn.cyberpeace.mp.collection.config;

import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

@Configuration
public class CollectionEsConfig {
    @Resource
    private CollectionConfig collectionConfig;

    @Bean(name="restHighLevelClient")
    public RestHighLevelClient wazuhRestHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(this.createWazuHttpHost()));
    }

    private HttpHost[] createWazuHttpHost() {
        String esNode = collectionConfig.getEsNode();
        Asserts.check(!ObjectUtils.isEmpty(esNode), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = esNode.split(",");
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
