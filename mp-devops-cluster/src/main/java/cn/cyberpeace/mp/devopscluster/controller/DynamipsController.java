 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DynamipsController.java.
 // File size 2095.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.DynamipsConverter;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.DynamipsInstancePageRequest;
import cn.cyberpeace.mp.devopscluster.vo.InstancePageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

/**
 * @ClassName DynamipsController
 * @Description 虚拟机管理类
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/dynamips")
@Api(tags = "计算实例管理-dynamips")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.CLUSTER_MANAGEMENT)
@Deprecated
public class DynamipsController {

    private final DynamipsConverter dynamipsConverter;

    private final CsceneSpecialService csceneSpecialService;

    private final UserService userService;

    private final DeviceTargetMapper deviceTargetMapper;

    /**
     * 分页查询dynamips信息
     *
     * @param request
     * @return //
     */
    @ApiOperation("分页查询dynamips信息")
    @GetMapping("/page")
    @Deprecated
    public PageResult<InstancePageVO> page(DynamipsInstancePageRequest request) {
        return new PageResult<>(Collections.emptyList(), 0);
    }
}
