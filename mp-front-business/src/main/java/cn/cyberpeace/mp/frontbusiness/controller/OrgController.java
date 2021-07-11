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


@Api(value = "组织管理接口(业务)", tags = "AUTH-组织管理接口(业务)")
@Slf4j
@RestController("orgFrontController")
@RequestMapping(ControllerConstant.BASE_API_PATH + "/orgs")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @ApiOperation(value = "根据parent组织获取下级组织", notes = "")
    @ApiImplicitParam(name = "parentId", value = "上级组织ID", dataTypeClass = String.class)
    @GetMapping("/byparent")
    public ServiceResult getByParent(@RequestParam String parentId) {
        List<OrgBO> orgs = orgService.listByParent(parentId);
        return ServiceResult.builder().source(orgs).build().asResult();
    }

    @ApiOperation(value = "根据组织路径查询", notes = "")
    @ApiImplicitParam(name = "path", value = "组织路径", dataTypeClass = String.class)
    @GetMapping("/bypath")
    public ServiceResult<OrgBO> getByPath(@RequestParam String path) {
        OrgBO orgBO = orgService.getByPath(path.trim());
        return ResultCommon.success(orgBO);
    }

    @ApiOperation(value = "查询组织（树形结构，orgId为空就是查所有组织树形结构）", notes = "")
    @ApiImplicitParam(name = "orgId", value = "组织ID", dataTypeClass = String.class)
    @GetMapping("/tree")
    public ServiceResult getOrgTree(@RequestParam(required = false) String orgId) {
        List<OrgDto> tree = orgService.getTree(orgId);
        return ServiceResult.builder().source(tree).build().asResult();
    }

}
