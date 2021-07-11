package cn.cyberpeace.mp.auth.service.fields;

import cn.cyberpeace.mp.auth.model.MenuTemplateModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import org.springframework.stereotype.Component;


@Component
public class MenuTemplateFields extends BaseModelFields {
    
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
