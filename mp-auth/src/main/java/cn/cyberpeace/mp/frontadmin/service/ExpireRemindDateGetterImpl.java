 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExpireRemindDateGetterImpl.java.
 // File size 1159.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service;

import cn.cyberpeace.mp.auth.service.ExpireRemindDateGetter;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.frontadmin.response.activation.ActivationResponse;
import cn.cyberpeace.mp.frontadmin.service.sys.SysActivationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @ClassName ExpireRemindDateGetterImpl
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see ExpireRemindDateGetter
 */
@Service
public class ExpireRemindDateGetterImpl implements ExpireRemindDateGetter {

    @Resource
    private SysActivationService activationService;

    /**
     * get
     *
     * @return
     */
    @Override
    public LocalDateTime get() {
        ActivationResponse content = activationService.status();
        LocalDateTime delayRemindLicenseTime = TimeUtils.stringToLocalDateTime(content.getExpiredDate() + " 23:59:59");
        delayRemindLicenseTime = delayRemindLicenseTime.minusDays(31);
        return delayRemindLicenseTime;
    }
}
