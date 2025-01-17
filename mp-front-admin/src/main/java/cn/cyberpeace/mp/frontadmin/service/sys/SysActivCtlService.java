 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysActivCtlService.java.
 // File size 658.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;

 /**
  * sys活性细胞毒性t淋巴细胞服务
  *
  * @author 二竹
  * @date 2025/01/17
  */
 public interface SysActivCtlService extends BaseModelService<SysActivCtlModel, QueryParams> {

     /**
      * 选择有效
      *
      * @return {@link SysActivCtlModel }
      */
     SysActivCtlModel selectValid();

     /**
      * 选择未使用
      *
      * @return {@link SysActivCtlModel }
      */
     SysActivCtlModel selectUnused();

     /**
      * set all records' status = 0 except $
      * {
      * exceptedId
      * }
      *
      * @param exceptedId
      */
     void invalidOtherExceptOne(String exceptedId);

}
