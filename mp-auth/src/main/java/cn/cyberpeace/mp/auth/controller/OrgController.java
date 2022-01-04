package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 组织管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "组织管理接口", tags = "AUTH-组织管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/orgs")
public class OrgController extends BaseModelController<OrgModel, QueryParams, OrgService> {

    @Autowired
    public OrgController(OrgService modelService) {
        setModelService(modelService);
    }

    @Override
    @ApiOperation("创建组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "创建组织", target = OpTargetEnums.ORG)
    @PostMapping
    public ObjServiceResult create(@RequestBody OrgModel obj, @RequestParam(required = false) String[] fields) {
        return super.create(obj, fields);
    }

    @Override
    @ApiOperation("更新指定id组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新组织", target = OpTargetEnums.ORG)
    @PatchMapping("/{id}")
    public ObjServiceResult update(@PathVariable String id, @RequestBody OrgModel obj,
                                      @RequestParam(required = false) String[] fields) {
        return super.update(id, obj, fields);
    }

    @Override
    @ApiOperation("删除指定id组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除组织", target = OpTargetEnums.ORG)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return super.delete(id);
    }


    /*
    @ApiOperation(value = "根据parent组织获取下级组织", notes = "")
    @GetMapping("/byparent")
    public ServiceResult getByParent(@RequestParam String parentId) {
        List<OrgModel> orgs = getModelService().listByParent(parentId);
        return ServiceResult.builder().source(orgs).build().asResult();
    }

    @ApiOperation(value = "查询组织（树形结构，orgId为空就是查所有组织树形结构）", notes = "")
    @GetMapping("/tree")
    public ServiceResult getOrgTree(@RequestParam(required = false) String orgId) {
        List<OrgDto> tree = getModelService().getTree(orgId);
        return ServiceResult.builder().source(tree).build().asResult();
    }
    */

}
