package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
public class OrgController extends BaseModelController<OrgModel, OrgService> {

    @Autowired
    public OrgController(OrgService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "获取所有parent组织", notes = "")
    @GetMapping("/parents")
    public ServiceResult getParentOrg() {
        List<OrgModel> orgs = getModelService().getParents();
        return ServiceResult.builder().source(orgs).build().asResult();
    }


    @ApiOperation(value = "根据parent组织获取下级组织", notes = "")
    @GetMapping("/byparent")
    public ServiceResult getByParent(@RequestParam String parentId) {
        List<OrgModel> orgs = getModelService().getByParent(parentId);
        return ServiceResult.builder().source(orgs).build().asResult();
    }




}
