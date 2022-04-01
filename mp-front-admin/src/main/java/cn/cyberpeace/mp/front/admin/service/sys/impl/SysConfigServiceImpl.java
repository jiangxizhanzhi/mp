package cn.cyberpeace.mp.front.admin.service.sys.impl;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.ParamCheck;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.admin.constant.AdminBasei18nCode;
import cn.cyberpeace.mp.front.admin.constant.AdminErrorCode;
import cn.cyberpeace.mp.front.admin.constant.SysConfigConstant;
import cn.cyberpeace.mp.front.admin.event.ConfigEventPublisher;
import cn.cyberpeace.mp.front.admin.event.SysConfigUpdateEvent;
import cn.cyberpeace.mp.front.admin.model.SysConfigModel;
import cn.cyberpeace.mp.front.admin.repository.SysConfigMapper;
import cn.cyberpeace.mp.front.admin.service.fields.SysConfigFields;
import cn.cyberpeace.mp.front.admin.service.sys.SysConfigService;
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
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Service
@Slf4j
public class SysConfigServiceImpl extends BaseModelServiceImpl<SysConfigModel, SysConfigMapper, SysConfigFields, QueryParams>
        implements SysConfigService {

    @Autowired
    public SysConfigServiceImpl(SysConfigMapper modelMapper, SysConfigFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigEventPublisher configEventPublisher;


    @Override
    public ObjServiceResult<SysConfigModel> create(SysConfigModel obj, String[] fields) {
        SysConfigModel config = getByKeyName(obj.getKeyName());
        if (Objects.nonNull(config)) {
            log.error("config {} already existed! can not add again!");
            throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_EXISTED.format(obj.getKeyName()));
        }
        return create(obj);
    }

    @Override
    @Transactional
    public ServiceResult<Integer> bulkCreate(List<SysConfigModel> objs) {
        for (SysConfigModel obj : objs) {
            create(obj);
        }
        return new ServiceResult<>(objs.size());
    }

    @Override
    public List<SysConfigModel> listAll() {
        return getModelMapper().selectAll();
    }

    @Override
    public SysConfigModel getByKeyName(String keyName) {
        ParamCheck.notEmpty(keyName, AdminBasei18nCode.SYS_CONFIG_KEY_NAME);

        return getModelMapper().selectByKey(keyName);
    }

    @Override
    public boolean updateByKeyName(SysConfigModel sysConfigModel) {
        ParamCheck.notEmpty(sysConfigModel.getKeyName(), AdminBasei18nCode.SYS_CONFIG_KEY_NAME);

        if (sysConfigModel.getKeyName().equals(SysConfigConstant.USER_AUDIT_KEY) && sysConfigModel.getValue().equals("0")) {
            // 如果要关闭审核功能，需要检查用户中有没有处于待审核状态的
            List<UserModel> list = userService.listNotAudited();
            if (CollectionUtil.isNotEmpty(list)) {
                throw new MpRuntimeException(AdminErrorCode.SYS_CONFIG_CANNOT_CLOSE_AUDIT);
            }
        }

        int cnt = getModelMapper().updateByKeyName(sysConfigModel.getKeyName(), sysConfigModel.getValue());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateByKeyname(List<SysConfigModel> configs) {
        try {
            for (SysConfigModel config : configs) {
                updateByKeyName(config);
            }
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            throw e;
        }

        return true;
    }


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

    @Override
    public boolean isNewUserNeedAudit() {
        SysConfigModel config = getByKeyName(SysConfigConstant.USER_AUDIT_KEY);
        if (Objects.nonNull(config) && Objects.equals(SysConfigConstant.USER_AUDIT_VALUE_YES, config.getValue())) {
            // 新用户需要审核
            return true;
        }
        return false;
    }

    @Override
    public void publishConfigUpdateEvent(List<SysConfigModel> configs) {
        if (CollectionUtil.isNotEmpty(configs)) {
            Map<String, String> configMap = configs2Map(configs);
            SysConfigUpdateEvent event = new SysConfigUpdateEvent(configMap);
            configEventPublisher.publishEvent(event);
        }
    }

    private Map<String, String> configs2Map(List<SysConfigModel> configs) {
        Map<String, String> configMap = new HashMap();
        configs.forEach(model -> {
            configMap.put(model.getKeyName(), model.getValue());
        });
        return configMap;
    }

}
