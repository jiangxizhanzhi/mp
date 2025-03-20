package cn.cyberpeace.mp.behavior.controller;


import cn.cyberpeace.mp.base.config.BaseAppConfig;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.mptool.FileUtil;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.req.ReportLogReq;
import cn.cyberpeace.mp.behavior.service.NetworkBehaviorService;
import cn.cyberpeace.mp.behavior.service.RunBehaviorLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@Slf4j
@Api(tags = "行为执行服务管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH)
public class BehaviorAgentController {

	@Autowired
	private NetworkBehaviorService networkBehaviorService;

	@Autowired
	private RunBehaviorLogService runBehaviorLogService;

	@Autowired
	private BaseAppConfig baseAppConfig;

	@ApiOperation("下载服务部署包")
	@GetMapping("/install")
	public void install(HttpServletResponse response) {
		String filePath = baseAppConfig.getResourcesDir() + File.separator + "behavior" + File.separator + "install.zip";
		FileUtil.setResponse(new File(filePath), false, response);
	}

	@ApiOperation("上报执行日志")
	@PostMapping("/report_log")
	public ServiceResult<Boolean> reportLog(@RequestBody @Validated ReportLogReq reportLogReq) {
		runBehaviorLogService.reportLog(reportLogReq);
		return new ServiceResult<>(true).asResult();
	}

	@ApiOperation("注册")
	@GetMapping("/register")
	public ServiceResult<Boolean> register(@ApiParam(name = "name", value = "队列名称") @RequestParam("name") String name) {
		runBehaviorLogService.register(name);
		return new ServiceResult<>(true).asResult();
	}
}
