package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.model.AvatarModel;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.SysConfigModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.repository.OrgMapper;
import cn.cyberpeace.mp.auth.repository.SysConfigMapper;
import cn.cyberpeace.mp.auth.service.AvatarService;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.SysConfigService;
import cn.cyberpeace.mp.auth.service.fields.OrgFields;
import cn.cyberpeace.mp.auth.service.fields.SysConfigFields;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.cluster.metadata.AliasValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private AvatarService avatarService;

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
    public boolean isUserAudit() {
        SysConfigModel config = getByKeyName("user_audit");
        if (Objects.nonNull(config) && Objects.equals("1", config.getValue())) {
            return true;
        }
        return false;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean init() {
//        avatarService.deleteAll();

//        String avatar01 = ResourceUtil.getResource("avatar/1.png", this.getClass()).getPath();

        // 默认头像
//        String avatar01 = Objects.requireNonNull(this.getClass().getResource("avatar/1.png")).getPath();

        InputStream avatar02 = Objects.requireNonNull(SysConfigServiceImpl.class.getClassLoader().getResourceAsStream("avatar/2.png"));
//        String avatar03 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("avatar/3.png")).getPath();
//        String avatar04 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("avatar/4.png")).getPath();
//        String avatar05 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("avatar/5.png")).getPath();
//        String avatar06 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("avatar/6.png")).getPath();
//        String avatar07 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("avatar/7.png")).getPath();
//        String avatar08 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("avatar/8.png")).getPath();
//        String avatar09 = Objects.requireNonNull(this.getClass().getClassLoader().getResource("avatar/9.png")).getPath();


        log.info("avatar02:{}", avatar02);
        List<InputStream> avatarlist = Arrays.asList(avatar02);
//        List<String> avatarlist = Arrays.asList(avatar01, avatar02, avatar03, avatar04, avatar05, avatar06, avatar07, avatar08, avatar09);

        for (InputStream ava : avatarlist) {
            String url = avatarService.uploadIs(ava, "2.png", null);
            AvatarModel avatarModel = new AvatarModel();
            avatarModel.setUrl(url);
            avatarService.create(avatarModel);
        }

        return true;
    }
}
