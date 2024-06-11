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

import java.util.List;

@Api(tags = "场景行为管理")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscene")
public class SceneBehaviorController {

	@Autowired
	private CsceneUserBehaviorService csceneUserBehaviorService;

	@PostMapping("/element")
	@ApiOperation("保存节点关联的行为")
	public ServiceResult<Boolean> addBehavior(@RequestBody @Validated ElementBehaviorReq addReq) {
		return ResultCommon.success(csceneUserBehaviorService.add(addReq));
	}

	@GetMapping("/element")
	@ApiOperation("查看节点关联的行为")
	public ServiceResult<List<CsceneNodeBehaviorRes>> getBehavior(@ApiParam(name = "node_id", value = "节点id", required = true) @RequestParam(name = "node_id") String nodeId) {
		return ResultCommon.success(csceneUserBehaviorService.get(nodeId));
	}
}
