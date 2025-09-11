package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.model.PlatformModel;
import cn.cyberpeace.mp.devopscluster.request.PlatformPageRequest;
import cn.cyberpeace.mp.devopscluster.vo.PlatformVO;
import cn.cyberpeace.mp.devopscluster.service.PlatformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @Description: 超融合平台管理接口
 * @Author guquan
 * @Date：2025/09/09 11:13
 */
@Api(value = "超融合平台管理接口", tags = "超融合平台管理接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/platform")
@AuthTarget(TargetEnum.PLATFORM_MANAGEMENT)
public class PlatformController {

    @Autowired
    private PlatformService platformService;

    @AuthTarget(TargetEnum.NO_AUTH)
    @ApiOperation("查询单个平台")
    @ApiImplicitParam(name = "id", value = "超融合平台ID", dataTypeClass = String.class)
    @GetMapping("/{id}")
    public ServiceResult<PlatformVO> get(@PathVariable String id) {
        ObjServiceResult<PlatformModel> result = platformService.get(id);
        if (Objects.nonNull(result.getSource())) {
            PlatformModel platformModel = result.getSource().getObj();
            PlatformVO platformVO = platformService.platformModel2PlatformVO(platformModel);
            return ResultCommon.success(platformVO);
        } else {
            throw new MpRuntimeException(DevopsClusterErrorCode.PLATFORM_NOT_EXIST.format(id));
        }
    }

    @ApiOperation("查询超融合平台列表")
    @GetMapping("/page")
    @AuthTarget(TargetEnum.NO_AUTH)
    public ServiceResult<PlatformVO> page(PlatformPageRequest pageRequest) {
        pageRequest.ensure();
        return platformService.page(pageRequest);
    }

    @ApiOperation("新增超融合平台")
    @ApiImplicitParam(name = "platformVO", value = "新增超融合平台", dataTypeClass = PlatformVO.class)
    @PostMapping
    public ServiceResult<PlatformVO> create(@RequestBody PlatformVO platformVO) {
        PlatformVO rtn = platformService.add(platformVO);
        return ResultCommon.success(rtn);
    }

    @ApiOperation("更新超融合平台")
    @ApiImplicitParam(name = "platformVO", value = "更新超融合平台", dataTypeClass = PlatformVO.class)
    @PostMapping("/modify")
    public ServiceResult modify(@RequestBody PlatformVO platformVO) {
        PlatformVO rtn = platformService.modify(platformVO);
        return ResultCommon.success(rtn);
    }

    @ApiOperation("删除指定id超融合平台")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    public ServiceResult<Boolean> delete(@PathVariable String id) {
        Boolean rtn = platformService.deletePlatform(id);
        return ResultCommon.success(rtn);
    }
}
