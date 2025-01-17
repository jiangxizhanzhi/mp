 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigFields.java.
 // File size 1309.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.fields;

import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;

 /**
  * sys配置字段
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Component
public class SysConfigFields extends BaseModelFields {
     /**
      * 所有字段
      */
     public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(SysConfigModel.class));

     /**
      * 可用字段
      */
     public static final DynamicFields availableFields = new DynamicFields(SysConfigModel::getTenant, SysConfigModel::getKeyName,
            SysConfigModel::getValue).merge(BaseModelFields.availableFields);

     /**
      * 隐藏字段
      */
     public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

     /**
      * 只读字段
      */
     public static final DynamicFields readonlyFields = new DynamicFields().merge(BaseModelFields.readonlyFields);

     /**
      *
      */
     public static final DynamicFields searchFields = new DynamicFields(SysConfigModel::getTenant, SysConfigModel::getKeyName, SysConfigModel::getValue);

     /**
      * 订单字段
      */
     public static final DynamicFields orderFields = new DynamicFields().merge(BaseModelFields.readonlyFields);
}
