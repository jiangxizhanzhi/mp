package cn.cyberpeace.mp.behavior.service;

import cn.cyberpeace.mp.base.util.http.HttpClientUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@CacheConfig(cacheNames = { "remoteService" })
public class RemoteService {

    private String SCENE_RETRIEVE = "http://172.30.103.38:8083/api/cscene/cscene_instances/";

    @Cacheable(key = "#p0")
    public Map<String, String> getIdIpMap(String sceneId) {
        JSONObject sceneTopo = getById(sceneId);
        JSONArray elements = sceneTopo.getJSONObject("scene")
                .getJSONObject("topology").getJSONArray("elements");
        Map<String, String> idIpMap = new HashMap<>();
        for (int i = 0 ; i < elements.size() ; i++) {
            JSONObject element = elements.getJSONObject(i);
            String id = element.getString("id");
            String ip = element.getJSONArray("ports").getJSONObject(0).getString("ip");
            idIpMap.put(id, ip);
        }
        return idIpMap;
    }

    public JSONObject getById(String sceneId) {
        String targetUrl = SCENE_RETRIEVE + "?id=" + sceneId;
        return replaceAccessIp(HttpClientUtil.rawGet(targetUrl, JSONObject.class));

    }

    private JSONObject replaceAccessIp(JSONObject topoData) {
        if (topoData == null) {
            return null;
        }
        try {
            JSONObject elementPorts = topoData.getJSONObject("scene").getJSONObject("plugins")
                    .getJSONObject("cpcloud").getJSONObject("element_ports");
            replaceAccessIpExe(elementPorts);
        } catch (Exception e) {
            log.error("[getById] access ip replacement failed:", e);
        }

        return topoData;
    }

    private void replaceAccessIpExe(JSONObject elementPorts) {
        if (elementPorts == null) {
            return;
        }
        for (String key : elementPorts.keySet()) {
            JSONArray accessModes = elementPorts.getJSONObject(key).getJSONArray("access_modes");
            if (!accessModes.isEmpty()) {
                accessModes.toJavaList(JSONObject.class).forEach(item -> {
                    // origin access port
                    String accessIp = item.getString("access_ip");
                    String accessPort = item.getString("access_port");
                    if (!StringUtils.isEmpty(accessIp)) {
                        // internet access port
                        String internetProxyIp = item.getString("public_net_proxy_ip");
                        Integer internetProxyPort = item.getInteger("public_net_proxy_port");
                        if (!StringUtils.isEmpty(internetProxyIp)) {
                            item.put("access_ip", internetProxyIp);
                            item.put("access_port", internetProxyPort);
                            log.debug("[replaceAccessIp] access ip replacement [{}:{}] -> [{}:{}]",
                                    accessIp, accessPort, internetProxyIp, internetProxyPort);
                        }
                    }
                });
            }
        }
    }
}
