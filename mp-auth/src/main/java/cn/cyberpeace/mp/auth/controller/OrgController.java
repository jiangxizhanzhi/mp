package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ObjResult;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 组织管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "组织管理接口", tags = "组织管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/orgs")
@AuthTarget(TargetEnum.USER_MANAGE)
public class OrgController {

    @Autowired
    private OrgService orgService;

    /**
     * 查询指定id组织
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "查询指定id组织(restful形式)", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class)
    })
    @GetMapping("/{id}")
    @AuthTarget(TargetEnum.USER_MANAGE)
    public ObjServiceResult get(@PathVariable String id) {
        ObjServiceResult<OrgModel> orgModelObjServiceResult = orgService.get(id).asResult();
        OrgModel orgModel = orgModelObjServiceResult.getData();
        return new ObjServiceResult<>(new ObjResult<>(orgService.orgModel2OrgBO(orgModel), null)).asResult();
    }

    /**
     * 查询指定id组织
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "查询指定id组织", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class)
    })
    @GetMapping
    @AuthTarget(TargetEnum.USER_MANAGE)
    public ObjServiceResult getByParam(@RequestParam String id) {
        ObjServiceResult<OrgModel> orgModelObjServiceResult = orgService.get(id).asResult();
        OrgModel orgModel = orgModelObjServiceResult.getData();
        return new ObjServiceResult<>(new ObjResult<>(orgService.orgModel2OrgBO(orgModel), null)).asResult();
    }

    /**
     * 新增组织
     *
     * @param obj
     * @param fields
     * @return
     */
    @ApiOperation("新增组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "创建组织", target = OpTargetEnums.ORG)
    @PostMapping
    @AuthTarget(TargetEnum.ADMIN)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult create(@RequestBody OrgModel obj, @RequestParam(required = false) String[] fields) {
        ObjServiceResult<OrgModel> result = orgService.create(obj, fields);
        return result;
    }

    /**
     * 更新指定id组织
     *
     * @param id
     * @param obj
     * @param fields
     * @return
     */
    @ApiOperation("更新指定id组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新组织", target = OpTargetEnums.ORG)
    @PatchMapping("/{id}")
    @AuthTarget(TargetEnum.ADMIN)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult update(@PathVariable String id, @RequestBody OrgModel obj,
                                      @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return orgService.update(obj, fields);
    }

    /**
     * 删除指定id组织
     *
     * @param id
     * @return
     */
    @ApiOperation("删除指定id组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @AuthTarget(TargetEnum.ADMIN)
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除组织", target = OpTargetEnums.ORG)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return orgService.delete(id);
    }

}
