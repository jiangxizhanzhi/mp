 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name NetworkTrafficGroupController.java.
 // File size 2213.
 // File create date Fri Jul 12 12:55:27 CST 2024.
 // File update date Fri Jul 12 12:55:27 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.GroupRequest;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficGroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/flow_group")
@Api(value = "态势感知-场景的操作接口", tags = "态势感知-场景的操作接口")
@AuthTarget(TargetEnum.NO_AUTH)
public class NetworkTrafficGroupController {

    @Autowired
    private NetworkTrafficGroupService networkTrafficGroupService;

    /**
     * initArkimeNetwork
     *
     * @return 统一服务返回结果
     */
    @GetMapping("/init-ark-network")
    @ApiOperation(value = "初始化流量网络", notes = "")
    public ServiceResult initArkimeNetwork() {
        return ResultCommon.success();

    }

    /**
     * deleteModelById
     *
     * @param groupId
     * @return 统一服务返回结果
     */
    @DeleteMapping("/{groupId}")
    @ApiOperation(value = "根据groupId删除分组信息", notes = "")
    @ApiImplicitParam(name = "groupId", value = "场景分组id", dataTypeClass = String.class)
    public ServiceResult deleteModelById(@PathVariable("groupId") String groupId) {
        return ResultCommon.success();
    }


    /**
     * addGroup
     *
     * @param groupRequest
     * @return 统一服务返回结果
     */
    @PostMapping
    @ApiOperation(value = "新增分组信息", notes = "")
    @ApiImplicitParam(name = "groupRequest", value = "新增请求体", dataType = "body", dataTypeClass = GroupRequest.class)
    public ServiceResult addGroup(@RequestBody @Valid GroupRequest groupRequest) {
        return ResultCommon.success();
    }
}
