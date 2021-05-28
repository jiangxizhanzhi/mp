package cn.cyberpeace.mp.devops.cluster.service.impl;

import cn.cyberpeace.mp.base.service.impl.BaseModelServiceImpl;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAnalyseModel;
import cn.cyberpeace.mp.devops.cluster.repository.DeviceAnalyseMapper;
import cn.cyberpeace.mp.devops.cluster.service.DeviceAnalyseService;
import cn.cyberpeace.mp.devops.cluster.service.fields.DeviceAnalyseFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 运维管控-集群管理-计算节点-设备参数分析业务逻辑层
 * @author wanggang
 */
@Service
public class DeviceAnalyseServiceImpl extends BaseModelServiceImpl<DeviceAnalyseModel, DeviceAnalyseMapper, DeviceAnalyseFields>
        implements DeviceAnalyseService {

    @Autowired
    public DeviceAnalyseServiceImpl(DeviceAnalyseMapper modelMapper, DeviceAnalyseFields modelFields) {
        setModelMapper(modelMapper);
        setModelFields(modelFields);
    }

}
