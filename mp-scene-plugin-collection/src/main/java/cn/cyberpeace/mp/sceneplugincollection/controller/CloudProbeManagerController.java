package cn.cyberpeace.mp.sceneplugincollection.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.sceneplugincollection.bo.ProbeBO;
import cn.cyberpeace.mp.sceneplugincollection.service.CloudProbeManagerService;
import cn.cyberpeace.mp.situationaware.model.dto.probe.ProbePageQueryRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 云场景探针实时管理
 * @Author ling_yu
 * @Date 2024/3/28 14:42
 */
@Api(value = "云场景探针实时管理", tags = "云场景探针实时管理")
@Slf4j
@RestController
@RequestMapping("/cloud_probe_manager")
public class CloudProbeManagerController {

    @Autowired
    private CloudProbeManagerService cloudProbeManagerService;

    //探针列表查询
    @ApiOperation("探针列表查询")
    @GetMapping("/queryCloudProbe")
    public ServiceResult<List<ProbeBO>> queryCloudProbe(ProbePageQueryRequest probeQueryRequest) {
        //暂定查询条件
        //探针名称
        String name = probeQueryRequest.getName();
        //系统类型0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other, 只有一个
        Integer osType = probeQueryRequest.getOsTypes() == null ? null : probeQueryRequest.getOsTypes()[0];
        //作用类型0:信息类 1:事件类 2:日志类，只有一个
        Integer probeCategory = probeQueryRequest.getProbeCategories() == null ? null : probeQueryRequest.getProbeCategories()[0];
        log.info("queryCloudProbe, name:{}, osType:{}, probeCategory:{}", name, osType, probeCategory);
        //TODO 功能逻辑待补充
        List<ProbeBO> list = new ArrayList<>();
        return  new ServiceResult<>(list).asResult();
    }

    //查询探针状态
    @ApiOperation("查询探针状态")
    @GetMapping("/queryCloudProbeState")
    public ServiceResult<ProbeBO> queryCloudProbeState(ProbePageQueryRequest probeQueryRequest) {
        //只有一个
        String id = probeQueryRequest.getIds() == null ? null : probeQueryRequest.getIds()[0];
        log.info("id:{}", id);
        //TODO 功能逻辑待补充
        ProbeBO probeBO = new ProbeBO();
        return new ServiceResult<>(probeBO).asResult();
    }

    //探针安装
    @ApiOperation("探针安装")
    @PostMapping("/installCloudProbe")
    public ServiceResult<T> installCloudProbe(@RequestBody ProbePageQueryRequest probeQueryRequest) {
        //只有一个
        String id = probeQueryRequest.getIds() == null ? null : probeQueryRequest.getIds()[0];
        log.info("id:{}", id);
        //TODO 功能逻辑待补充
        return ResultCommon.success();
    }

    //探针重启
    @ApiOperation("探针重启")
    @PostMapping("/restartCloudProbe")
    public ServiceResult<T> restartCloudProbe(@RequestBody ProbePageQueryRequest probeQueryRequest) {
        //只有一个
        String id = probeQueryRequest.getIds() == null ? null : probeQueryRequest.getIds()[0];
        log.info("id:{}", id);
        //TODO 功能逻辑待补充
        return ResultCommon.success();
    }

    //探针卸载
    @ApiOperation("探针卸载")
    @PostMapping("/uninstallCloudProbe")
    public ServiceResult<T> uninstallCloudProbe(@RequestBody ProbePageQueryRequest probeQueryRequest) {
        //只有一个
        String id = probeQueryRequest.getIds() == null ? null : probeQueryRequest.getIds()[0];
        log.info("id:{}", id);
        //TODO 功能逻辑待补充
        return ResultCommon.success();
    }

    //探针删除
    @ApiOperation("探针删除")
    @PostMapping("/deleteCloudProbe")
    public ServiceResult<T> deleteCloudProbe(@RequestBody ProbePageQueryRequest probeQueryRequest) {
        //只有一个
        String id = probeQueryRequest.getIds() == null ? null : probeQueryRequest.getIds()[0];
        log.info("id:{}", id);
        //TODO 功能逻辑待补充
        return ResultCommon.success();
    }



}
