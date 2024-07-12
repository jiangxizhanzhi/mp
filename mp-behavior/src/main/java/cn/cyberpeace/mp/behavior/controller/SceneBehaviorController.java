 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneBehaviorController.java.
 // File size 2602.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.req.ElementBehaviorReq;
import cn.cyberpeace.mp.behavior.res.CsceneNodeBehaviorRes;
import cn.cyberpeace.mp.behavior.service.CsceneUserBehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @ClassName SceneBehaviorController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(tags = "场景行为管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene")
public class SceneBehaviorController {

	@Autowired
	private CsceneUserBehaviorService csceneUserBehaviorService;

	/**
	 * 保存节点关联的行为
	 *
	 * @param addReq
	 * @return
	 */
	@PostMapping("/element")
	@ApiOperation("保存节点关联的行为")
	public ServiceResult<Boolean> addBehavior(@RequestBody @Validated ElementBehaviorReq addReq) {
		return ResultCommon.success(csceneUserBehaviorService.add(addReq));
	}

	/**
	 * 查看节点关联的行为
	 *
	 * @param nodeId
	 * @return
	 */
	@GetMapping("/element")
	@ApiOperation("查看节点关联的行为")
	public ServiceResult<List<CsceneNodeBehaviorRes>> getBehavior(@ApiParam(name = "node_id", value = "节点id", required = true) @RequestParam(name = "node_id") String nodeId) {
		return ResultCommon.success(csceneUserBehaviorService.get(nodeId));
	}

	/**
	 * 获取节点的行为资源包
	 *
	 * @param response
	 * @param nodeId
	 * @param sceneId
	 * @return
	 */
	@GetMapping("/element/resource")
	@ApiOperation("获取节点的行为资源包")
	public ServiceResult<Boolean> getResource(HttpServletResponse response,
											  @ApiParam(name = "node_id", value = "节点id", required = true) @RequestParam(name = "node_id") String nodeId,
											  @ApiParam(name = "scene_id", value = "场景id", required = true) @RequestParam(name = "scene_id") String sceneId) {
		csceneUserBehaviorService.getElementResource(response, nodeId, sceneId);
		return ResultCommon.success(true);
	}
}
