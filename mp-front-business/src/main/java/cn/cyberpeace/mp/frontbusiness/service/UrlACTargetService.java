 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UrlACTargetService.java.
 // File size 439.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontbusiness.model.UrlACTargetModel;

 /**
  * urlactarget服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface UrlACTargetService extends BaseModelService<UrlACTargetModel, QueryParams> {

     /**
      * 按url酸删除
      *
      * @param urlacId urlac id
      * @return int
      */
     int deleteByUrlACId(String[] urlacId);
}
