 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClientDetailsService.java.
 // File size 456.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;

 /**
  * 客户端细节服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface ClientDetailsService extends BaseModelService<ClientDetailsModel, QueryParams> {

     /**
      * 获取客户端id
      *
      * @param clientId 客户机id
      * @return {@link ClientDetailsModel }
      */
     ClientDetailsModel getByClientId(String clientId);

}
