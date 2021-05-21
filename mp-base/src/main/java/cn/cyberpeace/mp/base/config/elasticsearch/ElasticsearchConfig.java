package cn.cyberpeace.mp.base.config.elasticsearch;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

@Configuration
public class ElasticsearchConfig {

    private String scheme = "http";

    private List<String> ipAddressList = Lists.newArrayList("10.100.7.50:9200");

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(this.createHttpHost()));
    }

    /**
     * 创建 HttpHost 对象
     *
     * @return 返回 HttpHost 对象数组
     */
    private HttpHost[] createHttpHost() {
        Asserts.check(!CollectionUtils.isEmpty(ipAddressList), "ElasticSearch cluster ip address cannot empty");
        HttpHost[] httpHosts = new HttpHost[ipAddressList.size()];
        for (int i = 0, len = ipAddressList.size(); i < len; i++) {
            String ipAddress = ipAddressList.get(i);
            String[] values = ipAddress.split(":");

            String ip = values[0];
            int port = Integer.parseInt(values[1]);
            // 创建 HttpHost
            httpHosts[i] = new HttpHost(ip, port, scheme);
        }
        return httpHosts;
    }
}
