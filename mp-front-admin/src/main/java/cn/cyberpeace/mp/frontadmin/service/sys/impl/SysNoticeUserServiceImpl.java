 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeUserServiceImpl.java.
 // File size 2027.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
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

 /**
  * sys请注意用户服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
@Slf4j
public class SysNoticeUserServiceImpl extends BaseModelServiceImpl<SysNoticeUserModel, SysNoticeUserMapper, SysNoticeUserFields, QueryParams> implements SysNoticeUserService {

     /**
      * sys请注意用户服务impl
      *
      * @param modelMapper 模型映射器
      * @param modelFields 模型领域
      */
     @Autowired
    public SysNoticeUserServiceImpl(SysNoticeUserMapper modelMapper, SysNoticeUserFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

     /**
      * 按通知id删除
      *
      * @param noticeIds 注意id
      * @return int
      */
     @Override
    public int deleteByNoticeIds(List<String> noticeIds) {
        if (CollectionUtil.isEmpty(noticeIds)) {
            return 0;
        }
        HumanCondition condition = new HumanCondition().filterIn(SysNoticeUserModel::getNoticeId, noticeIds.toArray(String[]::new));
        return getModelMapper().filterDelete(condition);

    }

     /**
      * 按用户删除
      *
      * @param userId 用户id
      * @return int
      */
     @Override
    public int deleteByUser(String userId) {
        if (StringUtils.isBlank(userId)) {
            return 0;
        }
        HumanCondition condition = new HumanCondition().filterIn(SysNoticeUserModel::getUser, Collections.singletonList(userId).toArray(String[]::new));
        return getModelMapper().filterDelete(condition);
    }
}
