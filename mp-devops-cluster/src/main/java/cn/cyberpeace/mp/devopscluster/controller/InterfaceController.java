package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.model.InterfaceModel;
import cn.cyberpeace.mp.devopscluster.request.InterfacePageRequest;
import cn.cyberpeace.mp.devopscluster.vo.InterfaceVO;
import cn.cyberpeace.mp.devopscluster.service.InterfaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @Description: 接口管理接口
 * @Author guquan
 * @Date：2025/09/09 11:13
 */
@Api(value = "接口管理接口", tags = "接口管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/interface")
@AuthTarget(TargetEnum.INTERFACE_MANAGEMENT)
public class InterfaceController {

    @Autowired
    private InterfaceService interfaceService;

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("查询单个接口")
    @ApiImplicitParam(name = "id", value = "接口ID", dataTypeClass = String.class)
    @GetMapping("/{id}")
    public ServiceResult<InterfaceVO> get(@PathVariable String id) {
        ObjServiceResult<InterfaceModel> result = interfaceService.get(id);
        if (Objects.nonNull(result.getSource())) {
            InterfaceModel interfaceModel = result.getSource().getObj();
            InterfaceVO interfaceVO = interfaceService.interfaceModel2InterfaceVO(interfaceModel);
            return ResultCommon.success(interfaceVO);
        } else {
            throw new MpRuntimeException(DevopsClusterErrorCode.INTERFACE_NOT_EXIST.format(id));
        }
    }

    @ApiOperation("查询接口列表")
    @GetMapping("/page")
    @AuthTarget(TargetEnum.NO_AUTH)
    public ServiceResult<InterfaceVO> page(InterfacePageRequest pageRequest) {
        pageRequest.ensure();
        return interfaceService.page(pageRequest);
    }

    @ApiOperation("新增接口")
    @ApiImplicitParam(name = "interfaceVO", value = "新增接口", dataTypeClass = InterfaceVO.class)
    @PostMapping
    public ServiceResult<InterfaceVO> create(@RequestBody InterfaceVO interfaceVO) {
        InterfaceVO rtn = interfaceService.add(interfaceVO);
        return ResultCommon.success(rtn);
    }

    @ApiOperation("更新接口")
    @ApiImplicitParam(name = "interfaceVO", value = "更新接口", dataTypeClass = InterfaceVO.class)
    @PostMapping("/modify")
    public ServiceResult modify(@RequestBody InterfaceVO interfaceVO) {
        InterfaceVO rtn = interfaceService.modify(interfaceVO);
        return ResultCommon.success(rtn);
    }

    @ApiOperation("删除指定id接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        Boolean rtn = interfaceService.deleteInterface(id);
        return ResultCommon.success(rtn);
    }
}
