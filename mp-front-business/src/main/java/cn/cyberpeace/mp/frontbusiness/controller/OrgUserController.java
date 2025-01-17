 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgUserController.java.
 // File size 4961.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.frontbusiness.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.org.BindOrgUsersRequest;
import cn.cyberpeace.mp.auth.request.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.service.TenantUserService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

 /**
  * 组织用户控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "组织-用户管理接口(业务)", tags = "AUTH-组织-用户管理接口(业务)")
@Slf4j
@RestController("orgUserFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_users")
public class OrgUserController {

     /**
      * 租户用户服务
      */
     @Autowired
    private TenantUserService tenantUserService;

     /**
      * 通过org获取页面
      *
      * @param req 要求
      * @return {@link ListObjServiceResult }<{@link Object }>
      */
     @ApiOperation(value = "分页查询组织下所有用户信息（默认查询组织下所有层级子组织的用户，" +
            "可以通过设置showAllUnderLevel=false来关闭默认，只显示当前组织绑定的用户），附带组织，角色信息", notes = "")
    @GetMapping("/page/byorg")
    public ListObjServiceResult<Object> getPageByOrg(OrgUserPageRequest req) {
        req.ensure();
        PageInfo pageInfo = tenantUserService.pageUsersByOrg(req);

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

     /**
      * 按用户id列表
      *
      * @param ids id
      * @return {@link ServiceResult }<{@link List }<{@link UserBo }>>
      */
     @ApiOperation(value = "根据userId列表查询用户信息", notes = "")
    @PostMapping("/list_by_ids")
    public ServiceResult<List<UserBo>> listByUserIds(@RequestBody List<String> ids) {
        List<UserBo> list = tenantUserService.listByUserIds(ids);
        return ResultCommon.success(list);
    }

     /**
      * @param orgId org id
      * @return {@link ServiceResult }<{@link List }<{@link UserModel }>>
      */
     @ApiOperation(value = "查询组织下所有用户信息", notes = "")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/list/byorg")
    public ServiceResult<List<UserModel>> getByOrg(@RequestParam String orgId) {
        List<UserModel> users = tenantUserService.listUsersByOrgId(orgId);
        return ResultCommon.success(users);
    }

     /**
      * @param userId 用户id
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "根据userId查询所在组织信息（按照层级排列）", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = String.class)
    @GetMapping("/hierarchical/byuser")
    public ServiceResult listHierarchicalOrgs(@RequestParam String userId) {
        List<OrgDto> orgDtos = tenantUserService.listHierarchicalOrgsByUserId(userId);
        return ServiceResult.builder().source(orgDtos).build().asResult();
    }

     /**
      * @param bindOrgUsersReq
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "增量绑定组织-用户关系！", notes = "")
    @ApiImplicitParam(name = "bindOrgUsersReq", value = "绑定请求体", dataTypeClass = BindOrgUsersRequest.class)
    @PostMapping("/bind")
    public ServiceResult bindOrgUsersAdditional(@RequestBody @Validated BindOrgUsersRequest bindOrgUsersReq) {
        boolean success = tenantUserService.additionalBindOrgUsers(bindOrgUsersReq.getOrgId(), bindOrgUsersReq.getUserIds());
        if (success) {
            return ResultCommon.success();
        } else {
            log.error("bind org user failed!");
            throw new MpRuntimeException(AuthErrorCode.USER_ORG_ADD_ERROR);
        }
    }

     /**
      * @param userId 用户id
      * @return {@link ServiceResult }
      */
     @ApiOperation(value = "根据userId查询所有所在组织的路径 如： 一级组织/二级组织/三级组织", notes = "")
    @ApiImplicitParam(name = "userId", value = "用户ID", dataTypeClass = String.class)
    @GetMapping("/allorgpath/byuser")
    public ServiceResult getAllOrgPath(@RequestParam String userId) {
        String allOrgPath = tenantUserService.getAllOrgPathByUserId(userId);
        return ResultCommon.success(allOrgPath);
    }

     /**
      * @param orgId org id
      * @return {@link ServiceResult }<{@link Integer }>
      */
     @ApiOperation(value = "查询组织下的用户数量", notes = "")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/usercnt")
    public ServiceResult<Integer> getUserCnt(@RequestParam String orgId) {
        Integer userCnt = tenantUserService.getUserCnt(orgId);
        return ResultCommon.success(userCnt);
    }
}
