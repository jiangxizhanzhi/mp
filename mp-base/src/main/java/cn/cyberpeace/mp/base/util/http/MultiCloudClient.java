package cn.cyberpeace.mp.base.util.http;

import cn.cyberpeace.mp.base.config.BaseAppConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class MultiCloudClient {
    private final BaseAppConfig baseAppConfig;
    public final Map<Integer, HttpClient> CLOUD_CLIENT_MAP = new ConcurrentHashMap<>(8);


    public HttpClient getClient(int order) {

        return CLOUD_CLIENT_MAP.computeIfAbsent(order, k -> {
            return HttpClient.builder()
                    .configServer(getServer(order))
                    .build();
        });
    }

    private String getServer(int order) {
        String[] cloudUrls = baseAppConfig.getCloudUrls().split(";");
        return cloudUrls[order];
    }








}
