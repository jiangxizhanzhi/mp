 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigCheckService.java.
 // File size 1354.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadminbusiness.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.frontadminbusiness.constant.AdminBusinessErrorCode;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.constant.SysConfigKey;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

 /**
  * sys配置检查服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Slf4j
@Service
public class SysConfigCheckService {

     /**
      * 更新检查
      *
      * @param sysConfigs 系统配置
      */
     public void updateCheck(List<SysConfigModel> sysConfigs) {
        if (CollectionUtil.isEmpty(sysConfigs)) {
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
