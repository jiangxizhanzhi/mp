package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.model.DeviceModel;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceMapper;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.fields.DeviceFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 运维管控-集群管理-计算节点-设备管理业务逻辑层
 * @author wanggang
 */
@Service
public class DeviceServiceImpl extends BaseModelServiceImpl<DeviceModel, DeviceMapper, DeviceFields, QueryParams>
        implements DeviceService {

    @Autowired
    public DeviceServiceImpl(DeviceMapper modelMapper, DeviceFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
