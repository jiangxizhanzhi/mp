 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigServiceImpl.java.
 // File size 8550.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.MessageCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.constant.SysConfigConstant;
import cn.cyberpeace.mp.base.model.SysConfigModel;
import cn.cyberpeace.mp.base.repository.SysConfigMapper;
import cn.cyberpeace.mp.base.service.SysConfigService;
import cn.cyberpeace.mp.base.repository.fields.SysConfigFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName SysConfigServiceImpl
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelServiceImpl
 * @see SysConfigService
 */
@Service
@Slf4j
public class SysConfigServiceImpl extends BaseModelServiceImpl<SysConfigModel, SysConfigMapper, SysConfigFields, QueryParams>
        implements SysConfigService {

    /**
     * SysConfigServiceImpl
     *
     * @param modelMapper
     * @param modelFields
     */
    @Autowired
    public SysConfigServiceImpl(SysConfigMapper modelMapper, SysConfigFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }



    /**
     * create
     *
     * @param obj
     * @param fields
     * @return
     */
    @Override
    public ObjServiceResult<SysConfigModel> create(SysConfigModel obj, String[] fields) {
        SysConfigModel config = getByKeyName(obj.getKeyName());
        if (Objects.nonNull(config)) {
            log.error("config {} already existed! can not add again!", JsonCommon.stringify(config));
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }
        return create(obj);
    }

    /**
     * bulkCreate
     *
     * @param objs
     * @return
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
     * listAll
     *
     * @return
     */
    @Override
    public List<SysConfigModel> listAll() {
        return getModelMapper().list(new HumanCondition());
    }

    /**
     * getByKeyName
     *
     * @param keyName
     * @return
     */
    @Override
    public SysConfigModel getByKeyName(String keyName) {
        HumanCondition condition = new HumanCondition()
                .filterEqual(SysConfigModel::getKeyName, keyName);
        SysConfigModel model = getModelMapper().first(condition);
        if (model != null && "copyright".equals(model.getKeyName())) {
            String year = "@2021-" + LocalDate.now().getYear();
            String split = MessageCommon.get("admim.front.split");
            model.setValue(year + split + model.getValue());
            model.setInitValue(year + split + model.getInitValue());
        }
        return model;
    }

    /**
     * updateByKeyName
     *
     * @param sysConfigModel
     * @return boolean
     */
    @Override
    public boolean updateByKeyName(SysConfigModel sysConfigModel) {
        sysConfigModel.setInitValue(null);

        HumanCondition condition = new HumanCondition()
                .filterEqual(SysConfigModel::getKeyName, sysConfigModel.getKeyName());
        SysConfigModel newSysConfigModel = new SysConfigModel();
        newSysConfigModel.setValue(sysConfigModel.getValue());
        getModelMapper().filterUpdate(newSysConfigModel, condition);
        return true;
    }

    /**
     * batchUpdateByKeyname
     *
     * @param configs
     * @return boolean
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateByKeyname(List<SysConfigModel> configs) {
        try {
            for (SysConfigModel config : configs) {
                updateByKeyName(config);
            }
        } catch (Exception e) {
            log.error("batch update by key name error:", e);
            throw e;
        }

        return true;
    }


    /**
     * update
     *
     * @param obj
     * @param fields
     * @return
     */
    @Override
    public ObjServiceResult<SysConfigModel> update(SysConfigModel obj, String[] fields) {
        SysConfigModel oldConfigModel = getModelMapper().get(obj.getId());

        if (Objects.isNull(oldConfigModel)) {
            log.error("Config id:{} not existed!", obj.getId());
            throw new MpRuntimeException(BaseErrorCode.ERROR);
        }

        // 查看keyname是否是oldConfigModel的
        if (StringUtils.isNotBlank(obj.getKeyName())) {
            SysConfigModel configByKeyName = getByKeyName(obj.getKeyName());
            if (configByKeyName != null && !Objects.equals(configByKeyName.getId(), oldConfigModel.getId())) {
                log.error("Config {} already existed!", obj.getKeyName());
                throw new MpRuntimeException(BaseErrorCode.ERROR);
            }
        }

        return super.update(obj, fields);
    }

    /**
     * isNewUserNeedAudit
     *
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
     * publishConfigUpdateEvent
     *
     * @param configs
     */
    @Override
    public void publishConfigUpdateEvent(List<SysConfigModel> configs) {
        if (CollectionUtils.isNotEmpty(configs)) {
            Map<String, String> configMap = configs2Map(configs);
        }
    }

    /**
     * configs2Map
     *
     * @param configs
     * @return
     */
    private Map<String, String> configs2Map(List<SysConfigModel> configs) {
        Map<String, String> configMap = new HashMap();
        for (SysConfigModel model : configs) {
            configMap.put(model.getKeyName(), model.getValue());
        }
        return configMap;
    }

}
