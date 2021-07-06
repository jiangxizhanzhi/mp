package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "组织管理接口", tags = "OrgController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/orgs")
public class OrgController extends BaseModelController<OrgModel, QueryParams, OrgService> {

    @Autowired
    public OrgController(OrgService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "获取所有parent组织", notes = "")
    @GetMapping("/parents")
    public ServiceResult getParentOrg() {
        List<OrgModel> orgs = getModelService().listParents();
        return ServiceResult.builder().source(orgs).build().asResult();
    }


    @ApiOperation(value = "根据parent组织获取下级组织", notes = "")
    @GetMapping("/byparent")
    public ServiceResult getByParent(@RequestParam String parentId) {
        List<OrgModel> orgs = getModelService().listByParent(parentId);
        return ServiceResult.builder().source(orgs).build().asResult();
    }


    @ApiOperation(value = "查询所有组织树形结构", notes = "")
    @GetMapping("/tree")
    public ServiceResult getOrgTree(@RequestParam(required = false) String orgId) {
        List<OrgDto> tree = getModelService().getTree(orgId);
        return ServiceResult.builder().source(tree).build().asResult();
    }


    @ApiOperation(value = "根据orgId查询所在组织信息（按照层级排列）", notes = "")
    @GetMapping("/hierarchical")
    public ServiceResult listHierarchicalOrgs(@RequestParam String orgId) {
        OrgDto orgDto = getModelService().getHierarchicalOrgsByOrgId(orgId);
        return ServiceResult.builder().source(orgDto).build().asResult();
    }


    @ApiOperation(value = "根据orgId计算所在组织路径", notes = "")
    @GetMapping("/calculate/path")
    public ServiceResult getOrgPath(@RequestParam String orgId) {
        String path = getModelService().calculatePathByOrgId(orgId);
        return ServiceResult.builder().source(path).build().asResult();
    }

}
