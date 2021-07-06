package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.service.impl.ClientDetailsServiceImpl;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "Client管理接口", tags = "ClientDetailsController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/client_details")
public class ClientDetailsController extends BaseModelController<ClientDetailsModel, QueryParams, ClientDetailsService> {

    @Autowired
    public ClientDetailsController(ClientDetailsServiceImpl modelService) {
        setModelService(modelService);
    }

}
