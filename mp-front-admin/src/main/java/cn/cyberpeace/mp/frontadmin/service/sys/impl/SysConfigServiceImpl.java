 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigServiceImpl.java.
 // File size 7031.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontadmin.constant.AdminBasei18nCode;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.constant.SysConfigConstant;
import cn.cyberpeace.mp.frontadmin.event.ConfigEventPublisher;
import cn.cyberpeace.mp.frontadmin.event.SysConfigUpdateEvent;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.frontadmin.repository.SysConfigMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysConfigFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysConfigService;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

 /**
  * sys配置服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class SysConfigServiceImpl extends BaseModelServiceImpl<SysConfigModel, SysConfigMapper, SysConfigFields, QueryParams>
        implements SysConfigService {

     /**
      * sys配置服务impl
      *
      * @param modelMapper 模型映射器
      * @param modelFields 模型领域
      */
     @Autowired
    public SysConfigServiceImpl(SysConfigMapper modelMapper, SysConfigFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

     /**
      * 用户服务
      */
     @Autowired
    private UserService userService;

     /**
      * 配置事件publisher
      */
     @Autowired
    private ConfigEventPublisher configEventPublisher;

     /**
      * 创建
      *
      * @param obj    obj
      * @param fields 字段
      * @return {@link ObjServiceResult }<{@link SysConfigModel }>
      */
     @Override
    public ObjServiceResult<SysConfigModel> create(SysConfigModel obj, String[] fields) {
        SysConfigModel config = getByKeyName(obj.getKeyName());
        if (Objects.nonNull(config)) {
            log.error("config {} already existed! can not add again!");
            throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_EXISTED.format(obj.getKeyName()));
        }
        return create(obj);
    }

     /**
      * 批量创建
      *
      * @param objs obj
      * @return {@link ServiceResult }<{@link Integer }>
      */
     @Override
    @Transactional
    public ServiceResult<Integer> bulkCreate(List<SysConfigModel> objs) {
        for (SysConfigModel obj : objs) {
            create(obj);
        }
        return new ServiceResult<>(objs.size());
    }

     /**
      * 列出所有
      *
      * @return {@link List }<{@link SysConfigModel }>
      */
     @Override
    public List<SysConfigModel> listAll() {
        List<SysConfigModel> sysConfigModels = getModelMapper().list(new HumanCondition());
        return sysConfigModels;
    }

     /**
      * 按键名获取
      *
      * @param keyName 键名
      * @return {@link SysConfigModel }
      */
     @Override
    public SysConfigModel getByKeyName(String keyName) {
        ParamCheck.notEmpty(keyName, AdminBasei18nCode.SYS_CONFIG_KEY_NAME);
        HumanCondition condition = new HumanCondition()
                .filterEqual(SysConfigModel::getKeyName, keyName);
        SysConfigModel sysConfigModel = getModelMapper().first(condition);
        return sysConfigModel;
    }

     /**
      * 按键名更新
      *
      * @param sysConfigModel sys配置model
      * @return boolean
      */
     @Override
    public boolean updateByKeyName(SysConfigModel sysConfigModel) {
        ParamCheck.notEmpty(sysConfigModel.getKeyName(), AdminBasei18nCode.SYS_CONFIG_KEY_NAME);
        sysConfigModel.setInitValue(null);

        if (sysConfigModel.getKeyName().equals(SysConfigConstant.USER_AUDIT_KEY) && sysConfigModel.getValue().equals("0")) {
            // 如果要关闭审核功能，需要检查用户中有没有处于待审核状态的
            List<UserModel> list = userService.listNotAudited();
            if (CollectionUtil.isNotEmpty(list)) {
                throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_CANNOT_CLOSE_AUDIT);
            }
        }
        HumanCondition condition = new HumanCondition()
                .filterEqual(SysConfigModel::getKeyName, sysConfigModel.getKeyName());
        SysConfigModel newSysConfigModel = new SysConfigModel();
        newSysConfigModel.setValue(sysConfigModel.getValue());
        getModelMapper().filterUpdate(newSysConfigModel, condition);
        return true;
    }

     /**
      * @param configs
      * @return boolean
      */
     @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateByKeyname(List<SysConfigModel> configs) {
        try {
            for (SysConfigModel config : configs) {
                final boolean b = updateByKeyName(config);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            throw e;
        }

        return true;
    }

     /**
      * 更新
      *
      * @param obj    obj
      * @param fields 字段
      * @return {@link ObjServiceResult }<{@link SysConfigModel }>
      */
     @Override
    public ObjServiceResult<SysConfigModel> update(SysConfigModel obj, String[] fields) {
        ParamCheck.notBlank(obj.getId(), AdminBasei18nCode.SYS_CONFIG_ID);
        SysConfigModel oldConfigModel = getModelMapper().get(obj.getId());

        if (Objects.isNull(oldConfigModel)) {
            log.error("Config id:{} not existed!", obj.getId());
            throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_NOT_EXISTED.format(obj.getId()));
        }

        // 查看keyname是否是oldConfigModel的
        if (StringUtils.isNotBlank(obj.getKeyName())) {
            SysConfigModel configByKeyName = getByKeyName(obj.getKeyName());
            if (configByKeyName != null && !Objects.equals(configByKeyName.getId(), oldConfigModel.getId())) {
                log.error("Config {} already existed!", obj.getKeyName());
                throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_EXISTED.format(obj.getKeyName()));
            }
        }

        return super.update(obj, fields);
    }

     /**
      * @return boolean
      */
     @Override
    public boolean isNewUserNeedAudit() {
        SysConfigModel config = getByKeyName(SysConfigConstant.USER_AUDIT_KEY);
        if (Objects.nonNull(config) && Objects.equals(SysConfigConstant.USER_AUDIT_VALUE_YES, config.getValue())) {
            // 新用户需要审核
            return true;
        }
        return false;
    }

     /**
      * publish配置更新事件
      *
      * @param configs
      */
     @Override
    public void publishConfigUpdateEvent(List<SysConfigModel> configs) {
        if (CollectionUtil.isNotEmpty(configs)) {
            Map<String, String> configMap = configs2Map(configs);
            SysConfigUpdateEvent event = new SysConfigUpdateEvent(configMap);
            configEventPublisher.publishEvent(event);
        }
    }

     /**
      * @param configs
      * @return {@link Map }<{@link String }, {@link String }>
      */
     private Map<String, String> configs2Map(List<SysConfigModel> configs) {
        Map<String, String> configMap = new HashMap();
        configs.forEach(model -> {
            configMap.put(model.getKeyName(), model.getValue());
        });
        return configMap;
    }

}
