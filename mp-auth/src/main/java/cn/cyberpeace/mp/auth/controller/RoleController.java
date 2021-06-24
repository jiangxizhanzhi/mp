package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.service.RoleService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "角色管理接口", tags = "RoleController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/roles")
public class RoleController extends BaseModelController<RoleModel, RoleService> {

    @Autowired
    public RoleController(RoleService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "根据租户查询所有角色", notes = "")
    @GetMapping("/bytenant")
    public ServiceResult getRoleByTenant(@RequestParam String tenant) {
        List<RoleModel> roles = getModelService().listByTenantId(tenant);
        return ServiceResult.builder().source(roles).build().asResult();
    }




}
