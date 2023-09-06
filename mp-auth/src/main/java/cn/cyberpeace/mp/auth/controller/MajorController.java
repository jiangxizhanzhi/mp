package cn.cyberpeace.mp.auth.controller;


import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.MajorModel;
import cn.cyberpeace.mp.auth.request.major.MajorCreateRequest;
import cn.cyberpeace.mp.auth.request.major.MajorPageRequest;
import cn.cyberpeace.mp.auth.request.major.MajorUpdateRequest;
import cn.cyberpeace.mp.auth.response.major.MajorDto;
import cn.cyberpeace.mp.auth.service.MajorService;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 专业信息管理接口
 * @Author zhanzhi
 *
 */
@Api(value = "专业信息管理接口", tags = "AUTH-专业信息管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/major")
public class MajorController {

    @Autowired
    private MajorService majorService;

    @ApiOperation("新增专业信息")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.CREATE, title = "创建专业", target = OpTargetEnums.MAJOR)
    @PostMapping("/add")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<MajorModel> create(@RequestBody MajorCreateRequest majorCreateRequest) {
        return ResultCommon.success(majorService.createMajor(majorCreateRequest));
    }

    @ApiOperation("分页查询专业信息")
    @GetMapping("/page")
    public ListObjServiceResult<MajorDto> pageMajors(MajorPageRequest majorPageRequest) {
        return majorService.pageMajors(majorPageRequest);
    }

    @ApiOperation("删除专业信息")
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.DELETE, title = "删除专业", target = OpTargetEnums.MAJOR)
    public ServiceResult<Boolean> delete(@RequestBody List<String> ids) {
        return ResultCommon.success(majorService.deleteMajors(ids));
    }

    @ApiOperation("更新专业信息")
    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.ACCOUNT, type = OpTypeEnums.UPDATE, title = "更新专业", target = OpTargetEnums.MAJOR)
    public ServiceResult<MajorModel> update(@RequestBody MajorUpdateRequest majorUpdateRequest) {
        return ResultCommon.success(majorService.updateMajor(majorUpdateRequest));
    }

    @ApiOperation("获取所有专业名称和对应id")
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    public ServiceResult<List<MajorDto>> listAll() {
        return ResultCommon.success(majorService.listAllMajors());
    }

    @ApiOperation("获取指定id的专业详情")
    @GetMapping("/{id}")
    public ServiceResult<MajorDto> getMajorById(@PathVariable("id") String id) {
        return ResultCommon.success(MajorDto.convertModelToDto(majorService.getMajorById(id)));
    }


}
