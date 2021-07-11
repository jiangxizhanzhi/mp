package cn.cyberpeace.mp.devopscluster.cloud.platform.service;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.DynamicImageInfo;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualDynamips;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;


@Service
@Slf4j
public class DynamipsService {

    private VirtualDynamips virtualDynamips;

    public DynamipsService(@Autowired CloudSingleton cloudSingleton) {
        virtualDynamips = new VirtualDynamips();
    }

    public Map<String, Object> getTypeInfo() {
        return virtualDynamips.getTypeInfo().getItems();
    }

    public DynamicImageInfo getInfo(String platform) {
        var result = virtualDynamips.getPortInfo();
        DynamicImageInfo imageInfo = new DynamicImageInfo();
        imageInfo.setPlatforms(new ArrayList<>());
        imageInfo.setDetails(new ArrayList<>());
        for (var itemEntry : result.getItems().entrySet()) {
            if (StringUtils.isEmpty(platform) || StringUtils.equals(platform, itemEntry.getKey())) {
                imageInfo.getPlatforms().add(itemEntry.getKey());
                DynamicImageInfo.PlatformInfo platformInfo = new DynamicImageInfo.PlatformInfo();
                imageInfo.getDetails().add(platformInfo);
                platformInfo.setPlatform(itemEntry.getKey());
                platformInfo.setPortInfos(new ArrayList<>());
                for (var portNum : itemEntry.getValue().getPortSumList()) {
                    DynamicImageInfo.PlatformPortInfo platformPortInfo = new DynamicImageInfo.PlatformPortInfo();
                    platformPortInfo.setPortNum(portNum);
                    platformPortInfo.setPortNames(itemEntry.getValue().getPortList().subList(0, portNum));
                    platformInfo.getPortInfos().add(platformPortInfo);
                }
            }
        }
        return imageInfo;
    }

    public void reboot(String instanceId) {
        var result = virtualDynamips.reboot(instanceId);
        log.info("reboot {} dynamips result is {}", instanceId, result);
    }
}
