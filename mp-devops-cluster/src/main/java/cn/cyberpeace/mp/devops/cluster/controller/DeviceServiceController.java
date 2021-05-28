package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.DeviceServiceModel;
import cn.cyberpeace.mp.devops.cluster.service.DeviceServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运维管控-集群管理-计算节点-设备服务API层
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/device-service")
public class DeviceServiceController extends BaseModelController<DeviceServiceModel, DeviceServiceService> {

    @Autowired
    public DeviceServiceController(DeviceServiceService modelService) {
        setModelService(modelService);
    }
}
