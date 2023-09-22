package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.situationaware.model.dto.collection.WarningRuleConfigParams;
import org.springframework.stereotype.Component;

/**
 * @ClassName WarningRuleConfigFields
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 11:11
 **/
@Component
public class WarningRuleConfigFields extends BaseModelFields {

    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(WarningRuleConfigParams.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(WarningRuleConfigParams.class)) ;

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(BaseModel::getId, BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(WarningRuleConfigModel::getWarningTarget);

    public static final DynamicFields orderFields = new DynamicFields(BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

}
