 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name WazuhTokenInitJob.java.
 // File size 1425.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.situationaware.constant.SituationalAwareConstant;
import cn.cyberpeace.mp.situationaware.service.wazuh.SecurityService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 启动完后生命周期进行
 */
/**
 * @ClassName WazuhTokenInitJob
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Component
@Slf4j
public class WazuhTokenInitJob {

    @Autowired
    private SecurityService securityService;

    @MpInitiate(description = "wazuh token初始化", id = "wazuh_token_init")
    public void onApplicationEvent() {
        //定时更新token初始化
        SituationalAwareConstant.wazuhToken = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(14, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String key) {
                        String token = securityService.authenticate();
                        return token;
                    }
                });

    }


}
