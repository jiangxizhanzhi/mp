package cn.cyberpeace.mp.devopslog.config;

import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

/**
 * ES配置类
 * @author wanggang
 */
@Configuration(value = "LogElasticsearchConfig")
public class ElasticsearchConfig {

    @Autowired
    private DevopsLogConfig devopsLogConfig;

    @Bean(name="logRestHighLevelClient")
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(this.createHttpHost()));
    }

    /**cn.cyberpeace.mp.situationaware
     * 创建 HttpHost 对象
     *
     * @return 返回 HttpHost 对象数组
     */
    private HttpHost[] createHttpHost() {
        Asserts.check(!ObjectUtils.isEmpty(devopsLogConfig.getEsNodes()), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = devopsLogConfig.getEsNodes().split(",");
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
