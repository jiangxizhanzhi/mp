 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgController.java.
 // File size 2524.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

 /**
  * org控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "组织管理接口(业务)", tags = "AUTH-组织管理接口(业务)")
@Slf4j
@RestController("orgFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/orgs")
public class OrgController {

     /**
      * org服务
      */
     @Autowired
    private OrgService orgService;

     /**
      * 由父母提供
      *
      * @param parentId 父id
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "根据parent组织获取下级组织", notes = "")
    @ApiImplicitParam(name = "parentId", value = "上级组织ID", dataTypeClass = String.class)
    @GetMapping("/byparent")
    public ServiceResult getByParent(@RequestParam String parentId) {
        List<OrgBO> orgs = orgService.listByParent(parentId);
        return ServiceResult.builder().source(orgs).build().asResult();
    }

     /**
      * 按路径获取
      *
      * @param path 路径
      * @return {@link ServiceResult }<{@link OrgBO }>
      */
     @ApiOperation(value = "根据组织路径查询", notes = "")
    @ApiImplicitParam(name = "path", value = "组织路径", dataTypeClass = String.class)
    @GetMapping("/bypath")
    public ServiceResult<OrgBO> getByPath(@RequestParam String path) {
        OrgBO orgBO = orgService.getByPath(path.trim());
        return ResultCommon.success(orgBO);
    }

     /**
      * 获取组织树
      *
      * @param orgId org id
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "查询组织（树形结构，orgId为空就是查所有组织树形结构）", notes = "")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/tree")
    public ServiceResult getOrgTree(@RequestParam(required = false) String orgId) {
        List<OrgDto> tree = orgService.getTree(orgId);
        return ServiceResult.builder().source(tree).build().asResult();
    }

}
