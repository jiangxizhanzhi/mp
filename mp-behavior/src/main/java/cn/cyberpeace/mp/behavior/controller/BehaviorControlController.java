package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.req.CreateSimulateReq;
import cn.cyberpeace.mp.behavior.req.RunBehaviorReq;
import cn.cyberpeace.mp.behavior.res.RunBehaviorLogPage;
import cn.cyberpeace.mp.behavior.service.RunBehaviorLogService;
import cn.cyberpeace.mp.behavior.service.SimulateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "行为管理服务管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/control")
public class BehaviorControlController {

	@Autowired
	private RunBehaviorLogService runBehaviorLogService;

	@Autowired
	private SimulateService simulateService;

	@ApiOperation("执行行为")
	@PostMapping("/run_behavior")
	public ServiceResult<Boolean> runBehavior(@RequestBody RunBehaviorReq runBehaviorReq) {
		runBehaviorLogService.runBehavior(runBehaviorReq);
		return new ServiceResult<>(true).asResult();
	}

	@ApiOperation("注册的队列")
	@GetMapping("/queue_list")
	public ServiceResult<List<String>> getQueueList() {
		return new ServiceResult<>(runBehaviorLogService.getQueueList()).asResult();
	}

	@ApiOperation("创建行为任务")
	@PostMapping("/create")
	public ServiceResult<Boolean> createTask(@RequestBody CreateSimulateReq createSimulateReq) {
		simulateService.createTask(createSimulateReq);
		return new ServiceResult<>(true).asResult();
	}

	@ApiOperation("运行日志分页")
	@GetMapping("/run_log_page")
	public ListObjServiceResult<RunBehaviorLogPage> pageBehavior(QueryParams pageReq) {
		return runBehaviorLogService.page(pageReq).asResult();
	}
}
