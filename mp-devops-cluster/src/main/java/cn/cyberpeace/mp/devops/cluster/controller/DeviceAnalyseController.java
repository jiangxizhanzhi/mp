package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.model.DeviceAnalyseModel;
import cn.cyberpeace.mp.devops.cluster.service.DeviceAnalyseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 运维管控-集群管理-计算节点-设备参数分析API层
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/device-analyse")
public class DeviceAnalyseController extends BaseModelController<DeviceAnalyseModel, QueryParams, DeviceAnalyseService> {

    @Autowired
    public DeviceAnalyseController(DeviceAnalyseService modelService) {
        setModelService(modelService);
    }
}
