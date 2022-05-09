package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.model.OrgPermissionModel;
import cn.cyberpeace.mp.auth.service.OrgPermissionService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 组织-权限管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiIgnore
@Api(value = "组织-权限管理接口", tags = "AUTH-组织-权限管理接口")
@Slf4j
//@RestController
//@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_permissions")
public class OrgPermissionController extends BaseModelController<OrgPermissionModel, QueryParams, OrgPermissionService> {

    @Autowired
    public OrgPermissionController(OrgPermissionService modelService) {
        setModelService(modelService);
    }

}
