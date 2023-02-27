package cn.cyberpeace.mp.front.business.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 组织管理接口(业务)
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "组织管理接口(业务)", tags = "AUTH-组织管理接口(业务)")
@Slf4j
@RestController("orgFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/orgs")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserRoleService userRoleService;

    @ApiOperation(value = "根据parent组织获取下级组织", notes = "")
    @GetMapping("/byparent")
    public ServiceResult getByParent(@RequestParam String parentId) {
        List<OrgModel> orgs = orgService.listByParent(parentId);
        return ServiceResult.builder().source(orgs).build().asResult();
    }

    @ApiOperation(value = "查询组织（树形结构，orgId为空就是查所有组织树形结构）", notes = "")
    @GetMapping("/tree")
    public ServiceResult getOrgTree(@RequestParam(required = false) String orgId) {
        List<OrgDto> tree = orgService.getTree(orgId);
        return ServiceResult.builder().source(tree).build().asResult();
    }

    @ApiOperation(value = "查询当前用户下的组织（树形结构，orgId为空就是查所有组织树形结构）", notes = "")
    @GetMapping("/tree/current")
    public ServiceResult getOrgTreeCurrent() {
        UserModel loginUser = UserUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return ServiceResult.fail("get login user failed");
        }

        String orgId = StringUtils.EMPTY;
        boolean isSuperUser = loginUser.getId().equals(AuthConstant.ROOT_USER_ID);

        if (!isSuperUser) {
            // 非admin用户，根据组织id查询组织信息
            List<OrgModel> orgs = orgUserService.listOrgsByUserId(loginUser.getId());
            if (CollectionUtils.isEmpty(orgs)) {
                return ServiceResult.fail("[" + loginUser.getId() + "] user orgs is empty");
            }
            orgId = orgs.get(0).getId();
        }

        List<OrgDto> tree = orgService.getTree(orgId);
        return ServiceResult.builder().source(tree).build().asResult();
    }

}
