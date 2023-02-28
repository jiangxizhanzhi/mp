package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.BindOrgUsersRequest;
import cn.cyberpeace.mp.auth.dto.org.ListIdsByUserIdRequest;
import cn.cyberpeace.mp.auth.dto.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.RoleModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 组织用户管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "组织-用户管理接口(业务)", tags = "AUTH-组织-用户管理接口(业务)")
@Slf4j
@RestController("orgUserFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/org_users")
public class OrgUserController {

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserRoleService userRoleService;

    @ApiOperation(value = "分页查询组织下所有用户信息（默认查询组织下所有层级子组织的用户，" +
            "可以通过设置showAllUnderLevel=false来关闭默认，只显示当前组织绑定的用户），附带组织，角色信息", notes = "")
    @GetMapping("/page/byorg")
    public ListObjServiceResult<Object> getPageByOrg(OrgUserPageRequest req) {
        req.ensure();
        PageInfo pageInfo = orgUserService.pageUsersByOrg(req);

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @ApiOperation(value = "分页查询当前用户的组织下所有用户信息（默认查询组织下所有层级子组织的用户，" +
            "可以通过设置showAllUnderLevel=false来关闭默认，只显示当前组织绑定的用户），附带组织，角色信息", notes = "")
    @GetMapping("/page/byorg/current")
    public ListObjServiceResult<Object> getPageByOrgCurrent(OrgUserPageRequest req) {
        UserModel loginUser = UserUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            log.error("byorg get login user failed");
            throw new MpRuntimeException("byorg get login user failed");
        }

        if (StringUtils.isBlank(req.getOrg())) {
            // 是否admin用户
            boolean isSuperUser = loginUser.getId().equals(AuthConstant.ROOT_USER_ID);
            if (!isSuperUser) {
                // 非admin用户，根据组织id查询组织信息
                List<OrgModel> orgs = orgUserService.listOrgsByUserId(loginUser.getId());
                if (CollectionUtils.isEmpty(orgs)) {
                    log.error("byorg [" + loginUser.getId() + "] user orgs is empty");
                    throw new MpRuntimeException("byorg [" + loginUser.getId() + "] user orgs is empty");
                }
                req.setOrg(orgs.get(0).getId());
            }
        }


        req.ensure();
        PageInfo pageInfo = orgUserService.pageUsersByOrgExcludeManager(req, loginUser.getId());

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }

    @ApiOperation(value = "查询当前用户组织下的所有用户id")
    @GetMapping("/list/byUserId")
    public ServiceResult<List<String>> listByUserId(ListIdsByUserIdRequest request) {
        UserModel loginUser = UserUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            log.error("byUserId get login user failed");
            throw new MpRuntimeException("get login user failed");
        }

        List<UserModel> users;
        List<String> idList = new ArrayList<>();
        // 是否admin用户
        boolean isSuperUser = loginUser.getId().equals(AuthConstant.ROOT_USER_ID);

        if (!isSuperUser) {
            List<RoleModel> roles = userRoleService.listByUserId(loginUser.getId());
            // 是否管理员
            boolean isAdmin = roles.stream().anyMatch(r -> r.getIdentifier().equals(AuthConstant.ROLE_ADMIN));
            if (!isAdmin) {
                // 非管理员用户，直接返回自己的id
                log.warn("byUserId current user not a manager");
                return ServiceResult.success(Collections.singletonList(loginUser.getId()));
            }

            // 非admin用户，根据组织id查询组织信息
            List<OrgModel> orgs = orgUserService.listOrgsByUserId(loginUser.getId());
            if (CollectionUtils.isEmpty(orgs)) {
                log.error("byUserId [" + loginUser.getId() + "] user orgs is empty");
                throw new MpRuntimeException("byUserId [" + loginUser.getId() + "] user orgs is empty");
            }
            users = orgUserService.listUsersByOrgIdExcludeManager(orgs.get(0).getId(), loginUser.getId(), request.getUsername());
        } else {
            // 查询所有正常状态用户
            users = orgUserService.listAllEnabledUsers(request.getUsername());
        }

        if (!CollectionUtils.isEmpty(users)) {
            idList = users.stream().map(UserModel::getId).collect(Collectors.toList());
        }

        return ServiceResult.success(idList);
    }



    @ApiOperation(value = "查询组织下所有用户信息", notes = "")
    @GetMapping("/list/byorg")
    public ServiceResult<List<UserModel>> getByOrg(@RequestParam String orgId) {
        List<UserModel> users = orgUserService.listUsersByOrgId(orgId);
        return ServiceResult.success(users);
    }


    @ApiOperation(value = "根据userId查询所在组织信息（按照层级排列）", notes = "")
    @GetMapping("/hierarchical/byuser")
    public ServiceResult listHierarchicalOrgs(@RequestParam String userId) {
        List<OrgDto> orgDtos = orgUserService.listHierarchicalOrgsByUserId(userId);
        return ServiceResult.builder().source(orgDtos).build().asResult();
    }



    @ApiOperation(value = "增量绑定组织-用户关系！", notes = "")
    @PostMapping("/bind")
    public ServiceResult bindOrgUsersAdditional(@RequestBody @Validated BindOrgUsersRequest bindOrgUsersReq) {
        boolean success = orgUserService.additionalBindOrgUsers(bindOrgUsersReq.getOrgId(), bindOrgUsersReq.getUserIds());
        if (success) {
            return ServiceResult.successful();
        } else {
            log.error("bind org user failed!");
            throw new MpRuntimeException(AuthErrorCode.USER_ORG_ADD_ERROR);
        }
    }


    @ApiOperation(value = "根据userId查询所有所在组织的路径 如： 赛宁信息/产品研发中心/产品部,赛宁信息/产品研发中心/研发部", notes = "")
    @GetMapping("/allorgpath/byuser")
    public ServiceResult getAllOrgPath(@RequestParam String userId) {
        String allOrgPath = orgUserService.getAllOrgPathByUserId(userId);
        return ServiceResult.success(allOrgPath);
    }

    @ApiOperation(value = "查询组织下的用户数量", notes = "")
    @GetMapping("/usercnt")
    public ServiceResult<Integer> getUserCnt(@RequestParam String orgId) {
        Integer userCnt = orgUserService.getUserCnt(orgId);
        return ServiceResult.success(userCnt);
    }


}
