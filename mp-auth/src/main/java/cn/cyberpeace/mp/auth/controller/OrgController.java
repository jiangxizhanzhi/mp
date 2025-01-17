 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrgController.java.
 // File size 4461.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:13 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.service.OrgService;
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
  * org控制器
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Api(value = "组织管理接口", tags = "AUTH-组织管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/orgs")
public class OrgController {

     /**
      * org服务
      */
     @Autowired
    private OrgService orgService;

     /**
      * 得到
      *
      * @param id id
      * @return {@link ObjServiceResult }
      */
     @ApiOperation(value = "查询指定id数据", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class)
    })
    @GetMapping("/{id}")
    public ObjServiceResult get(@PathVariable String id) {
        ObjServiceResult<OrgModel> orgModelObjServiceResult = orgService.get(id).asResult();
        OrgModel orgModel = orgModelObjServiceResult.getData();
        return new ObjServiceResult<>(new ObjResult<>(orgService.orgModel2OrgBO(orgModel), null)).asResult();
    }

     /**
      * 通过参数获取
      *
      * @param id id
      * @return {@link ObjServiceResult }
      */
     @ApiOperation(value = "查询指定id数据", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class)
    })
    @GetMapping
    public ObjServiceResult getByParam(@RequestParam String id) {
        ObjServiceResult<OrgModel> orgModelObjServiceResult = orgService.get(id).asResult();
        OrgModel orgModel = orgModelObjServiceResult.getData();
        return new ObjServiceResult<>(new ObjResult<>(orgService.orgModel2OrgBO(orgModel), null)).asResult();
    }

     /**
      * 创建
      *
      * @param obj    obj
      * @param fields 字段
      * @return {@link ObjServiceResult }
      */
     @ApiOperation("创建组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "创建组织", target = OpTargetEnums.ORG)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult create(@RequestBody OrgModel obj, @RequestParam(required = false) String[] fields) {
        ObjServiceResult<OrgModel> result = orgService.create(obj, fields);
        return result;
    }

     /**
      * 更新
      *
      * @param id     id
      * @param obj    obj
      * @param fields 字段
      * @return {@link ObjServiceResult }
      */
     @ApiOperation("更新指定id组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
            @ApiImplicitParam(name="fields", value="需要返回的字段",dataTypeClass = String[].class),
    })
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新组织", target = OpTargetEnums.ORG)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ObjServiceResult update(@PathVariable String id, @RequestBody OrgModel obj,
                                      @RequestParam(required = false) String[] fields) {
        obj.setId(id);
        return orgService.update(obj, fields);
    }

     /**
      * 删除
      *
      * @param id id
      * @return {@link ServiceResult }<{@link Boolean }>
      */
     @ApiOperation("删除指定id组织")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除组织", target = OpTargetEnums.ORG)
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        return orgService.delete(id);
    }

}
