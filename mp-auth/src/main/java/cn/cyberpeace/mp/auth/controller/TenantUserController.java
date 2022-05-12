package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 租户用户管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiIgnore
@Api(tags = "AUTH-租户-用户管理接口")
@Slf4j
//@RestController
//@RequestMapping(ControllerConstant.BASE_API_PATH + "/tenant_users")
public class TenantUserController extends BaseModelController<TenantUserModel, QueryParams, TenantUserService> {

    @Autowired
    public TenantUserController(TenantUserService modelService) {
        setModelService(modelService);
    }

}
