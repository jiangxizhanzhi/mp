package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.front.admin.event.SysConfigUpdateEvent;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/4/1
 */
@Component
@Slf4j
public class EventListenerService {

    @Autowired
    private ScenePluginCloudSceneService pluginCloudSceneService;

    @EventListener
    public void onMyEventPublished(SysConfigUpdateEvent configUpdateEvent) {
        Map<String, String> configMap = (Map<String, String>) configUpdateEvent.getSource();
        log.info("configMap:{}", configMap);
        if (configMap.containsKey("vlan_bucket")) {
            String vlanRange = configMap.get("vlan_bucket");
            pluginCloudSceneService.changeVlanRange(vlanRange);
        }
    }
}
