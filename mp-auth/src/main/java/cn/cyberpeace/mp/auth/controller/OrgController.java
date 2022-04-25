package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.AuthLogConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "创建组织", target = AuthLogConstant.TARGET_ORG)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult create(@RequestBody OrgModel obj, @RequestParam(required = false) String[] fields) {
        return super.create(obj, fields);
    }

    @Override
    @ApiOperation("更新指定id组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新组织", target = AuthLogConstant.TARGET_ORG)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
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
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除组织", target = AuthLogConstant.TARGET_ORG)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return super.delete(id);
    }

    @ApiOperation(value = "获取所有parent组织", notes = "")
    @GetMapping("/parents")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getParentOrg() {
        List<OrgModel> orgs = getModelService().listParents();
        return ServiceResult.builder().source(orgs).build().asResult();
    }


    @ApiOperation(value = "根据parent组织获取下级组织", notes = "")
    @GetMapping("/byparent")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getByParent(@RequestParam String parentId) {
        List<OrgModel> orgs = getModelService().listByParent(parentId);
        return ServiceResult.builder().source(orgs).build().asResult();
    }


    @ApiOperation(value = "查询组织（树形结构，orgId为空就是查所有组织树形结构）", notes = "")
    @GetMapping("/tree")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult getOrgTree(@RequestParam(required = false) String orgId) {
        List<OrgDto> tree = getModelService().getTree(orgId);
        return ServiceResult.builder().source(tree).build().asResult();
    }


    @ApiOperation(value = "根据orgId查询所在组织信息（按照层级排列，直到顶级组织）", notes = "")
    @GetMapping("/hierarchical")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
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




    /**
     * 通用查询方法
     * @param queryParams 查询参数
     * @return 列表查询结果
     */
    @Override
    @ApiOperation("获取分页数据")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ListObjServiceResult<OrgModel> list(QueryParams queryParams) {
        handleQueryParams("list", queryParams);
        return getModelService().list(queryParams).asResult();
    }

    /**
     * 通用单条数据id查询
     * @param id 数据id
     * @param fields 返回数据字段
     * @return 单条数据结果
     */
    @Override
    @ApiOperation("获取指定id数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult<OrgModel> get(@PathVariable String id, @RequestParam(required = false) String[] fields) {
        log.info("id:{}, fields:{}", id, fields);
        return getModelService().get(id, fields).asResult();
    }



    /**
     * 通用批量创建
     * @param objs 创建对象集
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量创建数据")
    @PostMapping("/bulk_create")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkCreate(@RequestBody List<OrgModel> objs) {
        return getModelService().bulkCreate(objs).asResult();
    }

    /**
     * 通用批量更新
     * @param objs 更新对象集，需要更新的字段必须一致
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量更新数据")
    @PatchMapping("/bulk_update")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkUpdate(@RequestBody List<OrgModel> objs) {
        return getModelService().bulkUpdate(objs).asResult();
    }

    /**
     * 通用批量删除
     * @param ids 数据id集
     * @return 成功数量
     */
    @Override
    @ApiOperation("批量删除数据")
    @DeleteMapping("/bulk_delete")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return getModelService().bulkDelete(ids).asResult();
    }
}
