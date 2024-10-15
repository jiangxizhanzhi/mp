 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VmController.java.
 // File size 11633.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.VmConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devopscluster.request.ReviseContainerOrVmRequest;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import cn.cyberpeace.mp.devopscluster.vo.InstancePageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName VmController
 * @Description 虚拟机管理类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/instance")
@Api(tags = "计算实例管理-虚拟机")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.CLUSTER_MANAGEMENT)
public class VmController {

    private final VmConverter vmConverter;

    private final CsceneSpecialService csceneSpecialService;

    private final DeviceTargetMapper deviceTargetMapper;

    /**
     * 重启
     *
     * @param request
     * @return
     */
    @ApiOperation("重启虚拟机")
    @PostMapping("/restart")
    public ServiceResult<Boolean> restart(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.restart(request.getId())).asResult();
    }

    /**
     * 暂停
     *
     * @param request
     * @return
     */
    @ApiOperation("暂停虚拟机")
    @PostMapping("/pause")
    public ServiceResult<Boolean> pause(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.pause(request.getId())).asResult();
    }

    /**
     * 恢复
     *
     * @param request
     * @return
     */
    @ApiOperation("恢复虚拟机")
    @PostMapping("/unpause")
    public ServiceResult<Boolean> unpause(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.unpause(request.getId())).asResult();
    }

    /**
     * 开启
     *
     * @param request
     * @return
     */
    @ApiOperation("开启虚拟机")
    @PostMapping("/start")
    public ServiceResult<Boolean> start(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.start(request.getId())).asResult();
    }

    /**
     * 关闭
     *
     * @param request
     * @return
     */
    @ApiOperation("关闭虚拟机")
    @PostMapping("/stop")
    public ServiceResult<Boolean> stop(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.stop(request.getId())).asResult();
    }

    /**
     * 分页查询虚拟机信息
     *
     * @param request
     * @return
     */
    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/page")
    public PageResult<InstancePageVO> page(InstancePageRequest request) {
        BasePageBO<InstanceBO> model = vmConverter.page(new InstancePageDTO(request));
        List<InstancePageVO> list = convertBOToPageVO(model);
        return new PageResult<>(list, model.getTotal());
    }

    /**
     * 转换BO to VO
     *
     * @param model
     * @return
     */
    private List<InstancePageVO> convertBOToPageVO(BasePageBO<InstanceBO> model){
        List<InstanceBO> instances = model.getList();
        List<String> instanceNames = new ArrayList<>();
        for (InstanceBO instance : instances) {
            String name = instance.getName();
            instanceNames.add(name);
        }
        Map<String, String> uuid2UserId = new HashMap<>();
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(instanceNames);
        List<DeviceTargetPO> devopsResult = deviceTargetMapper.findByUuids(instanceNames);
        POLogoUtils.changeLogoPathToUrl(devopsResult);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();
        if (Objects.nonNull(source)) {
            List<CsceneNodeInfoBO> values = new ArrayList<>(source.values());
            if (CollectionUtils.isNotEmpty(values)) {
                List<String> userIdList = new ArrayList<>();
                for (CsceneNodeInfoBO value : values) {
                    String userId = value.getUserId();
                    userIdList.add(userId);
                }
            }
        }
        // 镜像管理实例的创建用户
        if (CollectionUtils.isNotEmpty(devopsResult)) {
            List<String> userIdList = new ArrayList<>();
            for (DeviceTargetPO targetPO : devopsResult) {
                if (StringUtils.isNotEmpty(targetPO.getInstanceCreateUser())) {
                    String instanceCreateUser = targetPO.getInstanceCreateUser();
                    userIdList.add(instanceCreateUser);
                }
            }
            Map<String, String> map = new HashMap<>();
            for (DeviceTargetPO deviceTargetPO : devopsResult) {
                if (StringUtils.isNotEmpty(deviceTargetPO.getInstanceCreateUser())) {
                    if (map.put(deviceTargetPO.getUuid(), deviceTargetPO.getInstanceCreateUser()) != null) {
                        throw new IllegalStateException("Duplicate key");
                    }
                }
            }
            uuid2UserId = map;
        }
        Map<String, String> uuid2User = uuid2UserId;
        List<InstancePageVO> list = new ArrayList<>();
        return list;
    }

}
