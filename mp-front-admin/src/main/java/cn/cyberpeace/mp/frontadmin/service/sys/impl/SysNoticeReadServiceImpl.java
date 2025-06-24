 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysNoticeReadServiceImpl.java.
 // File size 3227.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.repository.condition.aggregation.Count;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.constant.AdminConstant;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeReadModel;
import cn.cyberpeace.mp.frontadmin.repository.SysNoticeReadMapper;
import cn.cyberpeace.mp.frontadmin.service.SysNoticeReadService;
import cn.cyberpeace.mp.frontadmin.service.fields.SysNoticeReadFields;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

 /**
  * sys请注意读服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
public class SysNoticeReadServiceImpl extends BaseModelServiceImpl<SysNoticeReadModel, SysNoticeReadMapper, SysNoticeReadFields, QueryParams>
    implements SysNoticeReadService {

     /**
      * sys请注意读服务impl
      *
      * @param sysNoticeReadMapper
      */
     @Autowired
    public SysNoticeReadServiceImpl(SysNoticeReadMapper sysNoticeReadMapper) {
        setModelMapper(sysNoticeReadMapper);
    }

     /**
      * 根据用户id和通知id进行选择
      *
      * @param user     用户
      * @param noticeId 注意id
      * @return {@link SysNoticeReadModel }
      */
     @Override
    public SysNoticeReadModel selectByUserIdAndNoticeId(String user, String noticeId) {
        HumanCondition condition = new HumanCondition();
        if (StringUtils.isNotBlank(user)) {
            condition = condition.filterEqual(SysNoticeReadModel::getUser, user);
        }
        if (StringUtils.isNotBlank(noticeId)) {
            condition = condition.filterEqual(SysNoticeReadModel::getNoticeId, noticeId);
        }
        SysNoticeReadModel model = getModelMapper().first(condition);
        return model;
    }

     /**
      * 增加读计数
      *
      * @param id id
      * @return int
      */
     @Override
    public int increaseReadCount(String id) {
        HumanCondition selectCondition = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getId, id);
        SysNoticeReadModel model = getModelMapper().first(selectCondition);
        SysNoticeReadModel newModel = new SysNoticeReadModel();
        newModel.setIsReaded(1);
        newModel.setReadCount(model.getReadCount() + 1);
        HumanCondition updateCondition = new HumanCondition()
                .filterEqual(SysNoticeReadModel::getId, id);
        int rows = getModelMapper().filterUpdate(newModel, updateCondition);
        return rows;
    }

     /**
      * 计数被读取或被联合国推送
      *
      * @param user 用户
      * @return int
      */
     @Override
    public int countIsReadedOrIsUnPush(String user) {
        HumanCondition condition = new HumanCondition()
        .filterEqual(SysNoticeReadModel::getUser, user)
        .filterOrStart()
                .orEqual(SysNoticeReadModel::getIsReaded, SysNoticeReadModel.NOTICE_READED)
                .orEqual(SysNoticeReadModel::getIsNeedPush, SysNoticeReadModel.NOTICE_NEED_PUSH)
                .filterOrEnd()
        .selectAs(Count.f(SysNoticeReadModel::getNoticeId).distinct(), AdminConstant.SYS_NOTICE_READ_COUNT_DISTINCT_NOTICEID);
        Map<String, Object> stringObjectMap = getModelMapper().originFirst(condition);
        return ((Long) stringObjectMap.get(AdminConstant.SYS_NOTICE_READ_COUNT_DISTINCT_NOTICEID)).intValue();
    }
}
