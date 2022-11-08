package cn.cyberpeace.mp.cscene.service.filed;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import org.springframework.stereotype.Component;

@Component
public class CsceneModelFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(CsceneModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(CsceneModel.class));

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(BaseModel::getId, BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

    public static final DynamicFields searchFields = new DynamicFields(CsceneModel::getName, CsceneModel::getReferenceMetaData);

    public static final DynamicFields orderFields = new DynamicFields(BaseModel::getCreateTime, BaseModel::getUpdateTime);
}
