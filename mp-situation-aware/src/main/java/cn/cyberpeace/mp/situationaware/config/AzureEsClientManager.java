package cn.cyberpeace.mp.situationaware.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.HttpHost;
import org.apache.http.util.Asserts;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class AzureEsClientManager {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    private static final Map<String, RestHighLevelClient> clientMap = new HashMap<>();

    @PostConstruct
    public void init() {
        initializeClients();
    }

    private void initializeClients() {
        clientMap.put("publicWazuhRestHighLevelClient", createClient(createPublicWazuHttpHost()));
        clientMap.put("privateWazuhRestHighLevelClient", createClient(createPrivateWazuHttpHost()));
        clientMap.put("mixWazuhRestHighLevelClient", createClient(createMixWazuHttpHost()));
        clientMap.put("datasandtableRestHighLevelClient", createClient(createArkimeHttpHost()));
    }

    public RestHighLevelClient getClient(String name) {
        return clientMap.get(name);
    }

    public void updateClient(String name, String esNodes) {
        if (clientMap.containsKey(name)) {
            RestHighLevelClient oldClient = clientMap.get(name);
            if (oldClient != null) {
                try {
                    oldClient.close();
                } catch (IOException e) {
                    log.error("Error closing old client", e);
                }
            }
            HttpHost[] httpHosts = createHttpHost(esNodes);
            RestHighLevelClient newClient = createClient(httpHosts);
            clientMap.put(name, newClient);
            log.info("Updated client: {}", name);
        } else {
            log.warn("Client not found: {}", name);
        }
    }

    private RestHighLevelClient createClient(HttpHost[] httpHosts) {
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    private HttpHost[] createPublicWazuHttpHost() {
        String wazuhEsNodes = situationAwareConfig.getPublicWazuhEsNodes();
        Asserts.check(!ObjectUtils.isEmpty(wazuhEsNodes), "ElasticSearch cluster ip address cannot empty");
        return createHttpHost(wazuhEsNodes);
    }

    private HttpHost[] createPrivateWazuHttpHost() {
        String wazuhEsNodes = situationAwareConfig.getPrivateWazuhEsNodes();
        Asserts.check(!ObjectUtils.isEmpty(wazuhEsNodes), "ElasticSearch cluster ip address cannot empty");
        return createHttpHost(wazuhEsNodes);
    }

    private HttpHost[] createMixWazuHttpHost() {
        String wazuhEsNodes = situationAwareConfig.getMixWazuhEsNodes();
        Asserts.check(!ObjectUtils.isEmpty(wazuhEsNodes), "ElasticSearch cluster ip address cannot empty");
        return createHttpHost(wazuhEsNodes);
    }

    private HttpHost[] createArkimeHttpHost() {
        String elasticsearchNodes = situationAwareConfig.getArkimeEsNodes();
        Asserts.check(!ObjectUtils.isEmpty(elasticsearchNodes), "ElasticSearch cluster ip address cannot empty");
        return createHttpHost(elasticsearchNodes);
    }

    private HttpHost[] createHttpHost(String esNodes) {
        String[] ipArray = esNodes.split(",");
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
