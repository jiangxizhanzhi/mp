 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SecurityService.java.
 // File size 1038.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
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
        String requestUrl = situationAwareConfig.getWazuhBaseUrl() + "/security/user/authenticate?raw=true";
        return WazuhHttpsUtils.sendGetWithAuth(requestUrl, situationAwareConfig.getWazuhUsername(), situationAwareConfig.getWazuhPassword());
    }


}
