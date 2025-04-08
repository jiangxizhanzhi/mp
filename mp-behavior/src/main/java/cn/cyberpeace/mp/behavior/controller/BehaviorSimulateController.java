package cn.cyberpeace.mp.behavior.controller;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.constant.ControllerConstant;
import cn.cyberpeace.mp.behavior.req.DeleteReq;
import cn.cyberpeace.mp.behavior.req.SimulatePageReq;
import cn.cyberpeace.mp.behavior.res.SimulateDetailRes;
import cn.cyberpeace.mp.behavior.res.SimulatePageRes;
import cn.cyberpeace.mp.behavior.res.StatusRes;
import cn.cyberpeace.mp.behavior.res.TaskPageRes;
import cn.cyberpeace.mp.behavior.service.SimulateService;
import cn.cyberpeace.mp.behavior.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(tags = "行为仪表管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/simulate")
public class BehaviorSimulateController {

	@Autowired
	private SimulateService simulateService;

	@Autowired
	private TaskService taskService;

	@ApiOperation("行为仪表数量")
	@GetMapping("/count")
	public ServiceResult<Integer> countSimulate() {
		return new ServiceResult<>(simulateService.countSimulate()).asResult();
	}

	@ApiOperation("仿真任务数量")
	@GetMapping("/count_task")
	public ServiceResult<Integer> countTask() {
		return new ServiceResult<>(taskService.countTask()).asResult();
	}

	@ApiOperation("行为仪表分页")
	@GetMapping("/page")
	public ListObjServiceResult<SimulatePageRes> simulatePage(SimulatePageReq pageReq) {
		return simulateService.page(pageReq).asResult();
	}

	@ApiOperation("启动行为仪表")
	@GetMapping("/run")
	public ServiceResult<Boolean> runSimulate(@ApiParam(name = "simulate_id", value = "仪表id") @RequestParam("simulate_id") String simulateId) {
		simulateService.runSimulate(simulateId);
		return new ServiceResult<>(true).asResult();
	}

	@ApiOperation("停止行为仪表")
	@GetMapping("/stop")
	public ServiceResult<Boolean> stopSimulate(@ApiParam(name = "simulate_id", value = "仪表id") @RequestParam("simulate_id") String simulateId) {
		simulateService.stopSimulate(simulateId);
		return new ServiceResult<>(true).asResult();
	}

//	@ApiOperation("删除行为仪表")
//	@DeleteMapping("/delete")
//	public ServiceResult<Boolean> deleteSimulate(@RequestBody DeleteReq deleteReq) {
//		simulateService.deleteSimulate(deleteReq.getIds());
//		return new ServiceResult<>(true).asResult();
//	}

	@ApiOperation("查看仪表详情")
	@GetMapping("/detail")
	public ServiceResult<SimulateDetailRes> detail(@ApiParam(name = "simulate_id", value = "仪表id") @RequestParam("simulate_id") String simulateId) {
		return new ServiceResult<>(simulateService.detail(simulateId)).asResult();
	}

	@ApiOperation("实验名称列表")
	@GetMapping("/name_list")
	public ServiceResult<List<String>> getNameList() {
		return new ServiceResult<>(simulateService.getNameList()).asResult();
	}

	@ApiOperation("任务状态")
	@GetMapping("/status")
	public ServiceResult<List<StatusRes>> getStatusList() {
		return new ServiceResult<>(simulateService.getStatusList()).asResult();
	}

}
