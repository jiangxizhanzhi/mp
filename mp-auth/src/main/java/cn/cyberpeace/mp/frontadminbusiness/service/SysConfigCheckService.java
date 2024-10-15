 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigCheckService.java.
 // File size 1487.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadminbusiness.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.frontadminbusiness.constant.AdminBusinessErrorCode;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.constant.SysConfigKey;
import cn.cyberpeace.mp.base.model.SysConfigModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName SysConfigCheckService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/12/9 17:34
 * @Version 1.0.0
 */
@Slf4j
@Service
public class SysConfigCheckService {

    /**
     * updateCheck
     *
     * @param sysConfigs
     */
    public void updateCheck(List<SysConfigModel> sysConfigs) {
        if (CollectionUtils.isEmpty(sysConfigs)) {
            throw new MpRuntimeException(AdminErrorCode.PARAMS_ERROR);
        }

        for (SysConfigModel sysConfig : sysConfigs) {
            if (Objects.equals(sysConfig.getKeyName(), SysConfigKey.SCENE_AUTO_CLEAN_INTERVAL_DAY)) {
                String value = sysConfig.getValue();
                Integer intVal = Integer.valueOf(value);
                if (intVal < 0) {
                    throw new MpRuntimeException(AdminBusinessErrorCode.SYS_CONFIG_ERROR);
                }
            }
        }

    }

}
