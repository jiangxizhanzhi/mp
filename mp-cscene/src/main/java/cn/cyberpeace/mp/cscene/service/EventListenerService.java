package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.auth.extensions.security.event.UserLogoutEvent;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.frontadmin.event.SysConfigUpdateEvent;
import cn.cyberpeace.mp.sceneplugincloud.service.ScenePluginCloudSceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Slf4j
public class EventListenerService {

    @Autowired
    private ScenePluginCloudSceneService pluginCloudSceneService;

    @EventListener
    public void onMyEventPublished(SysConfigUpdateEvent configUpdateEvent) {
        Map<String, String> configMap = (Map<String, String>) configUpdateEvent.getSource();
        log.info("accept config update event , config info is {}", JsonCommon.stringify(configMap));
        if (configMap.containsKey("vlan_bucket")) {
            String vlanRange = configMap.get("vlan_bucket");
            pluginCloudSceneService.changeVlanRange(vlanRange);
        }
    }

    @EventListener
    public void onMyEventPublished(UserLogoutEvent userLogoutEvent) {
        Map<String, Object> userInfo = (Map<String, Object>) userLogoutEvent.getSource();
        log.info("accept user logout event , user info is {} ", JsonCommon.stringify(userInfo));
        if (userInfo.get("id") != null) {
            pluginCloudSceneService.expireAllRemoteConnect(userInfo.get("id").toString());
        }
    }
}
