package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.MenuConfigService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Api(value = "权限管理接口（业务）", tags = "AUTH-权限管理接口（业务）")
@Slf4j
@RestController("targetFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/targets")
public class TargetController {

    @Autowired
    @Qualifier("targetServiceImpl")

    private TargetService targetService;

    @Autowired
    private MenuConfigService menuConfigService;

    @Autowired
    private UserPermissionService userPermissionService;

    @ApiOperation(value = "查询权限树", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetId", value = "权限id", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "type", value = "权限类型 1：菜单，2：API，3：数据权限 不传默认为1：菜单", paramType = "query", dataTypeClass = Integer.class)
    })
    @GetMapping("/tree")
    public ServiceResult getTree(@RequestParam(required = false) String targetId, @RequestParam(required = false) Integer type) {
        List<TargetBo> tree = targetService.getTree(targetId, type);
        return ServiceResult.builder().source(tree).build().asResult();
    }


    @ApiOperation(value = "查询当前用户菜单")
    @GetMapping("/current_user/tree")
    public ServiceResult getTree() {
        List<String> menuIdents = userPermissionService.listMenuIdentsByLoginUser();
        var result = menuConfigService.getUserTreeMenuBy(UserUtils.getLoginUser().getId(), menuIdents);
        return ServiceResult.builder().source(result).build().asResult();
    }
}
