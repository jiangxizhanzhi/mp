package cn.cyberpeace.mp.devopscluster.service.fields;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.fields.DynamicFields;
import cn.cyberpeace.mp.base.service.fields.BaseModelFields;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.DeviceModel;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;

/**
 * @Description
 * @Author shgang
 * @Date 2022/12/20
 */
public class DeviceFields extends BaseModelFields {
    public static final DynamicFields allFields = new DynamicFields(ClassUtils.getBeanClassFields(DeviceModel.class));

    public static final DynamicFields availableFields = new DynamicFields(ClassUtils.getBeanClassFields(DeviceModel.class));

    public static final DynamicFields hiddenFields = allFields.subtract(availableFields);

    public static final DynamicFields readonlyFields = new DynamicFields(BaseModel::getId, BaseModel::getCreateTime,
            BaseModel::getUpdateTime, DeviceModel::getLeaks);

    public static final DynamicFields searchFields = new DynamicFields();

    public static final DynamicFields orderFields = new DynamicFields(BaseModel::getCreateTime,
            BaseModel::getUpdateTime);

}
