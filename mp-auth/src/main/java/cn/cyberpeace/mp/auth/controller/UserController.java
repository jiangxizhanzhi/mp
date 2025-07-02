 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserController.java.
 // File size 7190.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserLink;
import cn.cyberpeace.mp.auth.bo.user.UserSimpleBo;
import cn.cyberpeace.mp.auth.config.AuthConfig;
import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.extensions.security.common.JwtCommon;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.service.impl.ThirdSsoService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

 /**
  * 用户控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "用户管理接口", tags = "AUTH-用户管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/users")
public class UserController {

     /**
      * 用户服务
      */
     @Autowired
    private UserService userService;

     /**
      * 删除
      *
      * @param id id
      * @return {@link ServiceResult }<{@link Boolean }>
      */
     @ApiOperation("删除指定id用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除用户", target = OpTargetEnums.USER)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return userService.delete(id);
    }

     /**
      * 批量删除
      *
      * @param ids 数据id集
      * @return 成功数量
      */
     @ApiOperation("批量删除用户")
    @ApiImplicitParam(name="ids", value="用户id列表",dataTypeClass = String[].class)
    @DeleteMapping("/bulk_delete")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "批量删除用户", target = OpTargetEnums.USER)
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return userService.bulkDelete(ids).asResult();
    }

     /**
      * 得到
      *
      * @param id     id
      * @param fields 字段
      * @return {@link ObjServiceResult }<{@link UserBo }>
      */
     @ApiOperation(value = "查询指定id数据 附带组织，角色信息", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    public ObjServiceResult<UserBo> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        UserBo userBo = userService.getUserDto(id);
        return ResultUtils.obj2ObjServiceResult(userBo);
    }

     /**
      * 得到
      *
      * @param id id
      * @return {@link ObjServiceResult }<{@link UserSimpleBo }>
      */
     @ApiOperation(value = "查询指定id数据 返回有限数据，无登录用户权限拦截", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class)
    })
    @GetMapping("/no_restrict/{id}")
    public ObjServiceResult<UserSimpleBo> get(@PathVariable String id) {
        UserBo userBo = userService.getUserDtoNoRestrict(id);
        if (Objects.nonNull(userBo)) {
            UserSimpleBo userSimpleBo = new UserSimpleBo();
            BeanUtils.copyProperties(userBo, userSimpleBo);
            return ResultUtils.obj2ObjServiceResult(userSimpleBo);
        }

        return new ObjServiceResult(null).asResult();
    }

     /**
      * 得到
      *
      * @param ids id
      * @return {@link ServiceResult }<{@link List }<{@link UserSimpleBo }>>
      */
     @ApiOperation(value = "查询指定ids列表 返回有限数据，无登录用户权限拦截", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="ids", paramType="body", value="数据ids",dataTypeClass = List.class)
    })
    @PostMapping("/no_restrict/by_ids")
    public ServiceResult<List<UserSimpleBo>> get(@RequestBody List<String> ids) {
        var userModels = userService.getUserModelNoRestrictByUserIds(ids);

        List<UserSimpleBo> userSimpleBos = new ArrayList<>();
        userModels.forEach(userModel -> {
            UserSimpleBo userSimpleBo = new UserSimpleBo();
            BeanUtils.copyProperties(userModel, userSimpleBo);
            userSimpleBos.add(userSimpleBo);
        });

        return ResultCommon.success(userSimpleBos);
    }

     /**
      * 获取当前用户
      *
      * @return {@link ServiceResult }<{@link UserBo }>
      */
     @ApiOperation(value = "查询当前登录用户信息 附带组织，角色信息", notes = "")
    @GetMapping("/current")
    public ServiceResult<UserBo> getCurrentUser() {
        UserBo userBo = userService.getCurrentUserDto();
        return ResultCommon.success(userBo);
    }

     /**
      * 添加
      *
      * @param createRequest 创建请求
      * @return {@link ServiceResult }<{@link UserModel }>
      */
     @ApiOperation(value = "创建用户，同时完成组织，角色绑定关系", notes = "")
    @ApiImplicitParam(name = "createRequest", value = "请求", dataTypeClass = UserRequest.class)
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "新增用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> add(@RequestBody UserRequest createRequest) {
        createRequest.setAvatar(FsCommon.getFileKey(createRequest.getAvatar()));
        UserModel userModel = userService.add(createRequest);
        return ResultCommon.success(userModel);
    }

     /**
      * 修改
      *
      * @param modifyRequest
      * @return {@link ServiceResult }<{@link UserModel }>
      */
     @ApiOperation(value = "修改用户，同时完成组织，角色绑定关系", notes = "")
    @ApiImplicitParam(name = "modifyRequest", value = "修改请求", dataTypeClass = UserRequest.class)
    @PostMapping("/modify")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新用户", target = OpTargetEnums.USER)
    public ServiceResult<UserModel> modify(@RequestBody UserRequest modifyRequest) {
//        modifyRequest.setAvatar(FsCommon.getFileKey(modifyRequest.getAvatar()));
        UserModel userModel = userService.modify(modifyRequest);
        return ResultCommon.success(userModel);
    }

     @ApiOperation(value = "查询用户可跳转的子系统", notes = "")
     @GetMapping("/get_user_link")
     public ServiceResult<List<UserLink>> userLinks(HttpServletRequest request) {
         AuthConfig authConfig = BeanCommon.getBean(AuthConfig.class);
         if (StringUtils.isNotEmpty(authConfig.getSystemPage1())) {
             List<UserLink> result = new ArrayList<>();
             UserLink userLink1 = new UserLink();
             userLink1.setUrl(authConfig.getSystemPage1());
             userLink1.setName("活动空间");
             result.add(userLink1);

             UserLink userLink2 = new UserLink();
             userLink2.setUrl(authConfig.getSystemPage2());
             userLink2.setName("数字空间");
             result.add(userLink2);

             UserLink userLink3 = new UserLink();
             userLink3.setUrl(authConfig.getSystemPage3());
             userLink3.setName("训练数据");
             result.add(userLink3);
             return new ServiceResult<>(result).asResult();
         }
         String thirdToken = JwtCommon.getToken(request);
         ThirdSsoService thirdSsoService = BeanCommon.getBean(ThirdSsoService.class);
         var user = thirdSsoService.getuserinfoByLogintoken(thirdToken);
         List<ThirdSsoService.SystemLink> systemLinks = new ArrayList<>();
         for (var roleMenu : user.getRoleMenu()) {
             if (roleMenu.getSystems() != null) {
                 systemLinks.addAll(roleMenu.getSystems());
             }
         }
         // 去重
         Set<String> systemLinkSet = new HashSet<>();
         List<UserLink> userLinks = new ArrayList<>();
         for (ThirdSsoService.SystemLink systemLink : systemLinks) {
             if (!systemLinkSet.contains(systemLink.getSystemName())) {
                 systemLinkSet.add(systemLink.getSystemName());
                 UserLink userLink = new UserLink();
                 userLink.setUrl(systemLink.getRedirectUrl());
                 userLink.setName(systemLink.getSystemName());
                 userLinks.add(userLink);
             }
         }
         return new ServiceResult<>(userLinks).asResult();
     }
}
