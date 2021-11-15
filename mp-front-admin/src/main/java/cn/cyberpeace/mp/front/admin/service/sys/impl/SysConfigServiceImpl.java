package cn.cyberpeace.mp.front.admin.service.sys.impl;

import cn.cyberpeace.mp.front.admin.constant.AdminErrorCode;
import cn.cyberpeace.mp.front.admin.constant.SysConfigConstant;
import cn.cyberpeace.mp.front.admin.model.SysConfigModel;
import cn.cyberpeace.mp.front.admin.repository.SysConfigMapper;
import cn.cyberpeace.mp.front.admin.service.fields.SysConfigFields;
import cn.cyberpeace.mp.front.admin.service.sys.SysConfigService;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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


    @Override
    public ObjServiceResult<SysConfigModel> create(SysConfigModel obj, String[] fields) {
        throw new MpRuntimeException("不允许新增操作");
    }

    @Override
    public ServiceResult<Integer> bulkCreate(List<SysConfigModel> objs) {
        throw new MpRuntimeException("不允许新增操作");
    }

    @Override
    public List<SysConfigModel> listAll() {
        return getModelMapper().selectAll();
    }

    @Override
    public SysConfigModel getByKeyName(String keyName) {
        Assert.notBlank(keyName, "keyName不能为空！");
        return getModelMapper().selectByKey(keyName);
    }

    @Override
    public boolean updateByKeyName(SysConfigModel sysConfigModel) {
        Assert.notBlank(sysConfigModel.getKeyName(), "key不能为空！");
        Assert.notNull(sysConfigModel.getValue(), "value不能为null！");

        if (sysConfigModel.getKeyName().equals(SysConfigConstant.KEY_USER_AUDIT) && sysConfigModel.getValue().equals("0")) {
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
        Assert.notBlank(obj.getId(), "id不能为空！");
        SysConfigModel oldConfigModel = getModelMapper().get(obj.getId());

        if (Objects.isNull(oldConfigModel)) {
            throw new MpRuntimeException("id为:" + obj.getId() + "的系统配置不存在！");
        }

        // 查看keyname是否是oldConfigModel的
        if (StringUtils.isNotBlank(obj.getKeyName())) {
            SysConfigModel configByKeyName = getByKeyName(obj.getKeyName());
            if (configByKeyName != null && !Objects.equals(configByKeyName.getId(), oldConfigModel.getId())) {
                throw new MpRuntimeException("keyname已存在！" + obj.getKeyName());
            }
        }

        return super.update(obj, fields);
    }

    @Override
    public boolean isNewUserNeedAudit() {
        SysConfigModel config = getByKeyName(SysConfigConstant.KEY_USER_AUDIT);
        if (Objects.nonNull(config) && Objects.equals("1", config.getValue())) {
            // 新用户需要审核
            return true;
        }
        return false;
    }

}
