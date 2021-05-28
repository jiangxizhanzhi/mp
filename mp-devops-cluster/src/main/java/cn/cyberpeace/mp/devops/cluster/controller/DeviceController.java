package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.DeviceModel;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.fields.DeviceFields;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运维管控-集群管理-计算节点-设备管理API层
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/device")
public class DeviceController extends BaseModelController<DeviceModel, DeviceService> {

    private DeviceService deviceService;

    @Autowired
    public DeviceController(DeviceService modelService) {
        setModelService(modelService);
        this.deviceService = getModelService();
    }

    @GetMapping("/test")
    public void s(){
        System.out.println(DeviceFields.availableFields);
    }
}
