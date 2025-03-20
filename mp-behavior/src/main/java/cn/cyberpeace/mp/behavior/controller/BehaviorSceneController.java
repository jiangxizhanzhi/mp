package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.bo.NodeBehavior;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.service.SceneBehaviorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "场景行为配置")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/scene")
public class BehaviorSceneController {

	@Autowired
	private SceneBehaviorService sceneBehaviorService;

	@ApiOperation("查看节点行为配置")
	@GetMapping("/node_behavior")
	public ServiceResult<NodeBehavior> getNodeBehavior(@ApiParam(name = "scene_id", value = "场景id") @RequestParam("scene_id") String sceneId,
													   @ApiParam(name = "node_id", value = "节点id") @RequestParam("node_id") String nodeId) {
		return new ServiceResult<>(sceneBehaviorService.getNodeBehavior(sceneId, nodeId)).asResult();
	}

	@ApiOperation("保存节点行为配置")
	@PostMapping("/save_behavior")
	public ServiceResult<Boolean> saveBehavior(@RequestBody NodeBehavior nodeBehavior ) {
		sceneBehaviorService.save(nodeBehavior);
		return new ServiceResult<>(true).asResult();
	}


}
