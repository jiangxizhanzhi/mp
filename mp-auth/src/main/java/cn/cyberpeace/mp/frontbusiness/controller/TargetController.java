 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetController.java.
 // File size 4139.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.target.TargetBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.service.MenuConfigService;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.auth.service.UserPermissionService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.frontbusiness.config.FrontBusinessConfig;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName TargetController
 * @Description 权限管理接口
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
@Api(value = "权限管理接口", tags = "权限管理接口")
@Slf4j
@RestController("targetFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/targets")
public class TargetController {

    @Autowired
    @Qualifier("targetServiceImpl")
//    @Qualifier("templateTargetServiceImpl")
    private TargetService targetService;

    @Autowired
    private MenuConfigService menuConfigService;

    @Autowired
    private UserPermissionService userPermissionService;

    /**
     * 查询权限树
     *
     * @param targetId
     * @param type
     * @return 统一服务返回结果
     */
    @AuthTarget(TargetEnum.USER_MANAGE)
    @ApiOperation(value = "查询权限树", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetId", value = "权限id", paramType = "query", dataTypeClass = String.class),
            @ApiImplicitParam(name = "type", value = "权限类型 1：菜单，2：API，3：数据权限 不传默认为1：菜单", paramType = "query", dataTypeClass = Integer.class)
    })
    @GetMapping("/tree")
    public ServiceResult getTree(@RequestParam(required = false) String targetId, @RequestParam(required = false) Integer type) {
        List<TargetBo> tree = targetService.getTree(targetId, type);
        if (Boolean.FALSE.equals(BeanCommon.getBean(FrontBusinessConfig.class).getShowAi())) {
            for (TargetBo bo : tree) {
                if ("front_all".equals(bo.getId())) {
                    List<TargetBo> newChild = new ArrayList<>();
                    for (var c : bo.getChildren()) {
                        if (!"intelligence-center".equals(c.getId())) {
                            newChild.add(c);
                        }
                    }
                    bo.setChildren(newChild);
                }
            }
        }
        return ServiceResult.builder().source(tree).build().asResult();
    }


    /**
     * 查询当前用户菜单
     *
     * @return 统一服务返回结果
     */
    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation(value = "查询当前用户菜单")
    @GetMapping("/current_user/tree")
    public ServiceResult getTree() {
        List<String> menuIdents = userPermissionService.listMenuIdentsByLoginUser();
        if (Boolean.FALSE.equals(BeanCommon.getBean(FrontBusinessConfig.class).getShowAi())) {
            menuIdents.remove("intelligence-center");
        }
        List<TargetBo> result = menuConfigService.getUserTreeMenuBy(UserUtils.getLoginUser().getId(), menuIdents);
        return ServiceResult.builder().source(result).build().asResult();
    }
}
