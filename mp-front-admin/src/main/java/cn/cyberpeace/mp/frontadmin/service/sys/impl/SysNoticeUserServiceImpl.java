package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeUserMapper;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeUserFields;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeUserService;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
@Slf4j
public class SysNoticeUserServiceImpl extends BaseModelServiceImpl<SysNoticeUserModel, SysNoticeUserMapper, SysNoticeUserFields, QueryParams> implements SysNoticeUserService {

    @Autowired
    public SysNoticeUserServiceImpl(SysNoticeUserMapper modelMapper, SysNoticeUserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

    @Override
    public int deleteByNoticeIds(List<String> noticeIds) {
        if (CollectionUtil.isEmpty(noticeIds)) {
            return 0;
        }
        HumanCondition condition = new HumanCondition().filterIn(SysNoticeUserModel::getNoticeId, noticeIds.toArray(String[]::new));
        return getModelMapper().filterDelete(condition);

    }

    @Override
    public int deleteByUser(String userId) {
        if (StringUtils.isBlank(userId)) {
            return 0;
        }
        HumanCondition condition = new HumanCondition().filterIn(SysNoticeUserModel::getUser, Collections.singletonList(userId).toArray(String[]::new));
        return getModelMapper().filterDelete(condition);
    }
}
