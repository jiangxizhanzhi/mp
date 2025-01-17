 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysActivCtlFields.java.
 // File size 1353.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;
import org.springframework.stereotype.Component;

 /**
  * 系统激活CTL字段
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class SysActivCtlFields extends BaseModelFields {
     /**
      * 所有字段
      */
     public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysActivCtlModel.class));

     /**
      * 可用字段
      */
     public static final DynamicFields availableFields = new DynamicFields(SysActivCtlModel::getTenant, SysActivCtlModel::getSuffix,
            SysActivCtlModel::getSysTimestamp).merge(BaseModelFields.availableFields);

     /**
      * 隐藏字段
      */
     public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

     /**
      * 只读字段
      */
     public static final DynamicFields readonlyFields = new DynamicFields(TargetModel::getCreateTime,
            TargetModel::getUpdateTime);

     /**
      *
      */
     public static final DynamicFields searchFields = new DynamicFields(SysActivCtlModel::getTenant);

     /**
      *
      */
     public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
