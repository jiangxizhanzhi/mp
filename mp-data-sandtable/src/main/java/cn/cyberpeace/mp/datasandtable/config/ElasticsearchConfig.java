package cn.cyberpeace.mp.datasandtable.config;

import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Configuration
public class ElasticsearchConfig {

    @Value("${mp.datasandtable.es-nodes}")
    private String elasticsearchNodes;

    @Bean(name="datasandtableRestHighLevelClient")
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(this.createHttpHost()));
    }

    /**cn.cyberpeace.mp.datasandtable
     * 创建 HttpHost 对象
     *
     * @return 返回 HttpHost 对象数组
     */
    private HttpHost[] createHttpHost() {
        Asserts.check(!ObjectUtils.isEmpty(elasticsearchNodes), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = elasticsearchNodes.split(",");
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
