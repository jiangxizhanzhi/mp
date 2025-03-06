package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.req.RunBehaviorReq;
import cn.cyberpeace.mp.behavior.service.NetworkBehaviorTemplateService;
import cn.cyberpeace.mp.behavior.service.RunBehaviorLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "行为管理服务管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/control")
@Data
public class BehaviorControlController {

	@Autowired
	private NetworkBehaviorTemplateService networkBehaviorTemplateService;

	@Autowired
	private RunBehaviorLogService runBehaviorLogService;

	@ApiOperation("下发执行行为")
	@PostMapping("/run_behavior")
	public ServiceResult<Boolean> runBehavior(@RequestBody RunBehaviorReq runBehaviorReq) {
		runBehaviorLogService.runBehavior(runBehaviorReq);
		return new ServiceResult<>(true).asResult();
	}
}
