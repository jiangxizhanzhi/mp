 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACTargetServiceImpl.java.
 // File size 1299.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.service.impl;

import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontbusiness.fields.UrlACTargetFields;
import cn.cyberpeace.mp.frontbusiness.mapper.UrlACTargetMapper;
import cn.cyberpeace.mp.frontbusiness.model.UrlACTargetModel;
import cn.cyberpeace.mp.frontbusiness.service.UrlACTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

 /**
  * urlactarget服务impl
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Service
public class UrlACTargetServiceImpl extends BaseModelServiceImpl<UrlACTargetModel, UrlACTargetMapper, UrlACTargetFields, QueryParams>
    implements UrlACTargetService {

     /**
      * urlactarget服务impl
      *
      * @param urlACTargetMapper Url目标映射器
      */
     @Autowired
    public UrlACTargetServiceImpl(UrlACTargetMapper urlACTargetMapper) {
        setModelMapper(urlACTargetMapper);
    }

     /**
      * 按url酸删除
      *
      * @param urlacIds urlac id
      * @return int
      */
     @Override
    public int deleteByUrlACId(String[] urlacIds) {
        HumanCondition condition = new HumanCondition()
                .filterIn(UrlACTargetModel::getUrlacId, urlacIds);
        int rows = getModelMapper().filterDelete(condition);
        return rows;
    }
}
