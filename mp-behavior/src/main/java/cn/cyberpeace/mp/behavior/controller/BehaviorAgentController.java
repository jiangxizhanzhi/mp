package cn.cyberpeace.mp.behavior.controller;


import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.req.ReportLogReq;
import cn.cyberpeace.mp.behavior.service.NetworkBehaviorTemplateService;
import cn.cyberpeace.mp.behavior.service.RunBehaviorLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = "行为执行服务管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/agent")
@Data
public class BehaviorAgentController {

	@Autowired
	private NetworkBehaviorTemplateService networkBehaviorTemplateService;

	@Autowired
	private RunBehaviorLogService runBehaviorLogService;

	@ApiOperation("上报执行日志")
	@PostMapping("/report_log")
	public ServiceResult<Boolean> reportLog(ReportLogReq reportLogReq) {
		runBehaviorLogService.reportLog(reportLogReq);
		return new ServiceResult<>(true).asResult();
	}

	@ApiOperation("注册")
	@GetMapping("/register")
	public ServiceResult<Boolean> register(String queueName) {
		runBehaviorLogService.register(queueName);
		return new ServiceResult<>(true).asResult();
	}
}
