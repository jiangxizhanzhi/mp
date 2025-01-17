 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name AdminConfigService.java.
 // File size 1715.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.repository.AdminMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

 /**
  * 管理配置服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
public class AdminConfigService {

     /**
      * 密钥公共登记册
      */
     private static final String KEY_PUBLIC_REGISTER = "public_register";

     /**
      * 关键用户审计
      */
     private static final String KEY_USER_AUDIT = "user_audit";

     /**
      * 密钥注册用户默认角色
      */
     private static final String KEY_REGISTER_USER_DEFAULT_ROLE = "register_user_default_role";

     /**
      * 管理映射器
      */
     @Autowired
    private AdminMapper adminMapper;

     /**
      * 允许用户公开注册
      *
      * @return
      */
     public boolean isPublicRegisterAvailable() {
        String value = adminMapper.getConfigValue(KEY_PUBLIC_REGISTER);
        if (StringUtils.isNotBlank(value) && Objects.equals("1", value)) {
            // 允许注册
            return true;
        }
        // 不允许注册
        return false;
    }

     /**
      * 是否新注册用户需要审核
      *
      * @return
      */
     public boolean isNewUserNeedAudit() {
        String value = adminMapper.getConfigValue(KEY_USER_AUDIT);
        if (StringUtils.isNotBlank(value) && Objects.equals("1", value)) {
            // 新用户需要审核
            return true;
        }
        return false;
    }

     /**
      * 查询注册用户默认角色ID
      *
      * @return
      */
     public String getRegisterDefaultRoleId() {
        String value = adminMapper.getConfigValue(KEY_REGISTER_USER_DEFAULT_ROLE);
        return value;
    }

}
