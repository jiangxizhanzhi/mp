package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.BindOrgUsersRequest;
import cn.cyberpeace.mp.auth.dto.org.OrgUserPageRequest;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.util.ResultUtils;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
            throw new MpRuntimeException("get login user failed");
        }

        boolean isSuperUser = loginUser.getId().equals(AuthConstant.ROOT_USER_ID);

        if (!isSuperUser) {
            // 非admin用户，根据组织id查询组织信息
            List<OrgModel> orgs = orgUserService.listOrgsByUserId(loginUser.getId());
            if (CollectionUtils.isEmpty(orgs)) {
                throw new MpRuntimeException("[" + loginUser.getId() + "] user orgs is empty");
            }
            req.setOrg(orgs.get(0).getId());
        }

        req.ensure();
        PageInfo pageInfo = orgUserService.pageUsersByOrg(req);

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
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
