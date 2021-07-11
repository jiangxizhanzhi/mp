package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Slf4j
public class SecurityService {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    
    public String authenticate() {
        log.info("retrieve wazuh token...");
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + "/security/user/authenticate?raw=true";
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, situationAwareConfig.getWazuhUsername(), situationAwareConfig.getWazuhPassword());
    }


}
