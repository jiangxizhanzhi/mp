package cn.cyberpeace.mp.situationaware.service.wazuh;

import cn.cyberpeace.mp.situationaware.config.SituationAwareConfig;
import cn.cyberpeace.mp.situationaware.utils.WazuhHttpsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/23 10:29
 */
@Service
@Slf4j
public class SecurityService {

    @Autowired
    private SituationAwareConfig situationAwareConfig;

    /**
     * 查询wazuhToken防止token失效
     *
     * @return
     * @throws IOException
     */
    public String authenticate() {
        log.info("retrieve wazuh token...");
        var requestUrl = situationAwareConfig.getWazuhBaseUrl() + "/security/user/authenticate?raw=true";
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, situationAwareConfig.getWazuhUsername(), situationAwareConfig.getWazuhPassword());
    }


}
