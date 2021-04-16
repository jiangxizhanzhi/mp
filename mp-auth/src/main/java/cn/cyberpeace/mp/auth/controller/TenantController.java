package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.TenantModel;
import cn.cyberpeace.mp.auth.service.TenantService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/tenants")
public class TenantController extends BaseModelController<TenantModel, TenantService> {

    @Autowired
    public TenantController(TenantService modelService) {
        setModelService(modelService);
    }
}
