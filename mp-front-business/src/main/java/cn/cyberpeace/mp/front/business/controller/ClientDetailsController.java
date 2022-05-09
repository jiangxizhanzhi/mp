package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.model.ClientDetailsModel;
import cn.cyberpeace.mp.auth.service.ClientDetailsService;
import cn.cyberpeace.mp.auth.service.impl.ClientDetailsServiceImpl;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 客户端管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
//@ApiIgnore
@Api(value = "Client客户端管理接口(业务)", tags = "AUTH-Client客户端管理接口(业务)")
@Slf4j
//@RestController("clientDetailsFrontController")
//@RequestMapping(ControllerConstant.BASE_API_PATH + "/client_details")
public class ClientDetailsController extends BaseModelController<ClientDetailsModel, QueryParams, ClientDetailsService> {

    @Autowired
    public ClientDetailsController(ClientDetailsServiceImpl modelService) {
        setModelService(modelService);
    }

}
