 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SituationAwareEsConfig.java.
 // File size 2731.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Configuration
@Slf4j
public class SituationAwareEsConfig {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    @Bean(name="wazuhRestHighLevelClient")
    public RestHighLevelClient wazuhRestHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(this.createWazuHttpHost()));
    }

    @Bean(name="datasandtableRestHighLevelClient")
    public RestHighLevelClient datasandtableRestHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(this.createArkimeHttpHost()));
    }

    /**cn.cyberpeace.mp.situationaware
     * 创建 HttpHost 对象
     *
     * @return 返回 HttpHost 对象数组
     */
    private HttpHost[] createWazuHttpHost() {
        String wazuhEsNodes = situationAwareConfig.getWazuhEsNodes();
        Asserts.check(!ObjectUtils.isEmpty(wazuhEsNodes), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = wazuhEsNodes.split(",");
        HttpHost[] httpHosts = new HttpHost[ipArray.length];
        for (int i = 0, len = ipArray.length; i < len; i++) {
            String ipAddress = ipArray[i];
            String[] values = ipAddress.split(":");
            String ip = values[0];
            int port = Integer.parseInt(values[1]);
            httpHosts[i] = new HttpHost(ip, port, "http");
        }
        return httpHosts;
    }

    /**cn.cyberpeace.mp.situationaware
     * 创建 HttpHost 对象
     *
     * @return 返回 HttpHost 对象数组
     */
    private HttpHost[] createArkimeHttpHost() {
        String elasticsearchNodes = situationAwareConfig.getArkimeEsNodes();
        Asserts.check(!ObjectUtils.isEmpty(elasticsearchNodes), "ElasticSearch cluster ip address cannot empty");
        String[] ipArray = elasticsearchNodes.split(",");
        HttpHost[] httpHosts = new HttpHost[ipArray.length];
        for (int i = 0, len = ipArray.length; i < len; i++) {
            String ipAddress = ipArray[i];
            String[] values = ipAddress.split(":");
            String ip = values[0];
            int port = Integer.parseInt(values[1]);
            httpHosts[i] = new HttpHost(ip, port, "http");
        }
        return httpHosts;
    }

}
