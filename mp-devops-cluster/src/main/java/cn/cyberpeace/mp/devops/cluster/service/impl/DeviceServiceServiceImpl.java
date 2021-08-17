package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.DeviceServiceModel;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceServiceMapper;
import cn.cyberpeace.mp.devops.cluster.service.DeviceServiceService;
import cn.cyberpeace.mp.devops.cluster.service.fields.DeviceServiceFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 运维管控-集群管理-计算节点-设备服务业务逻辑层
 * @author wanggang
 */
@Service
public class DeviceServiceServiceImpl extends BaseModelServiceImpl<DeviceServiceModel, DeviceServiceMapper, DeviceServiceFields, QueryParams>
        implements DeviceServiceService {

    @Autowired
    public DeviceServiceServiceImpl(DeviceServiceMapper modelMapper, DeviceServiceFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
