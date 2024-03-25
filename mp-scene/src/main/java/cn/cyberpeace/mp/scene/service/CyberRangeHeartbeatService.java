package cn.cyberpeace.mp.scene.service;


import cn.cyberpeace.mp.scene.config.MultiSceneConfig;
import cn.cyberpeace.mp.scene.core.bo.MultiSceneRegisterBo;
import cn.cyberpeace.mp.scene.util.MultiSceneClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CyberRangeHeartbeatService {

    private final MultiSceneConfig multiSceneConfig;
    private final MultiSceneClient multiSceneClient;

    public CyberRangeHeartbeatService(MultiSceneConfig multiSceneConfig) {
        this.multiSceneConfig = multiSceneConfig;
        multiSceneClient = new MultiSceneClient();
    }

    public void registerCurrentRange() {
        if (multiSceneConfig.isSubCyberRangeFlag()) {
            multiSceneConfig.getManageCenterServerArray().forEach(manageServer -> {

                MultiSceneRegisterBo multiSceneRegisterBo = new MultiSceneRegisterBo();
                multiSceneRegisterBo.setRangeIp(multiSceneConfig.getCyberRangeIp());
                multiSceneRegisterBo.setRangePort(multiSceneConfig.getCyberRangePort());
                multiSceneRegisterBo.setRangeTag(multiSceneConfig.getCyberRangeTag());
                multiSceneClient.registerAndHeartbeat(manageServer, multiSceneRegisterBo);
            });

        }
    }
}
