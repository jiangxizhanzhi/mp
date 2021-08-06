package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.TenantModel;
import cn.cyberpeace.mp.auth.service.TenantService;
import cn.cyberpeace.mp.auth.service.param.TenantQueryParams;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "租户管理接口", tags = "AUTH-租户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/tenants")
public class TenantController extends BaseModelController<TenantModel, TenantQueryParams, TenantService> {

    @Autowired
    public TenantController(TenantService modelService) {
        setModelService(modelService);
    }

}
