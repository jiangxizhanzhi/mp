 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MenuTemplateFields.java.
 // File size 1726.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.MenuTemplateModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName MenuTemplateFields
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelFields
 */
@Component
public class MenuTemplateFields extends BaseModelFields {
    //public class TargetFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(MenuTemplateModel.class));

    public static final DynamicFields availableFields = new DynamicFields(MenuTemplateModel::getParent, MenuTemplateModel::getIdentifier,
            MenuTemplateModel::getMenuId,
            MenuTemplateModel::getTenant,
            MenuTemplateModel::getName, MenuTemplateModel::getPath, MenuTemplateModel::getType
    );

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(MenuTemplateModel::getCreateTime,
            MenuTemplateModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(MenuTemplateModel::getParent, MenuTemplateModel::getIdentifier, MenuTemplateModel::getTenant,
            MenuTemplateModel::getName, MenuTemplateModel::getPath);

    public static final DynamicFields orderFields = new DynamicFields(MenuTemplateModel::getId, MenuTemplateModel::getCreateTime,
            MenuTemplateModel::getUpdateTime);
}
