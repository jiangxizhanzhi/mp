package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.dto.OrgDto;
import cn.cyberpeace.mp.auth.dto.TargetDto;
import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "权限管理接口", tags = "TargetController")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/targets")
public class TargetController extends BaseModelController<TargetModel, QueryParams, TargetService> {

    @Override
    public ObjServiceResult<TargetModel> create(@Validated(AddGroup.class) @RequestBody TargetModel obj, @RequestParam(required = false) String[] fields) {
        return super.create(obj, fields);
    }

    @Autowired
    public TargetController(TargetService modelService) {
        setModelService(modelService);
    }


    @ApiOperation(value = "查询权限树形结构", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "targetId", value = "权限id", paramType = "form", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "权限类型 1：菜单，2：API，3：数据权限 不传默认为1：菜单", paramType = "form", dataType = "Integer")
    })
    @GetMapping("/tree")
    public ServiceResult getOrgTree(@RequestParam(required = false) String targetId, @RequestParam(required = false) Integer type) {
        List<TargetDto> tree = getModelService().getTree(targetId, type);
        return ServiceResult.builder().source(tree).build().asResult();
    }

}
