package cn.cyberpeace.mp.cscene.flow;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.request.flow.CsceneFlowTaskActionRequest;
import cn.cyberpeace.mp.cscene.request.flow.CsceneFlowTaskStartRequest;
import cn.cyberpeace.mp.flow.v2.bo.FlowTaskRecordBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/19
 */

@Api(value = "流量发生", tags = "流量发生")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/flow")
public class CsceneFlowController {
    @Resource
    private FlowBizService flowTaskService;

    @ApiOperation("启动")
    @PostMapping("/start")
    public ServiceResult<FlowTaskRecordBO> start(@RequestBody CsceneFlowTaskStartRequest request) {
        return flowTaskService.start(request.getFlowTaskId(), request.getFlowGeneratorId(), request.getTargetId());
    }

    @ApiOperation("暂停")
    @PostMapping("/pause")
    public ServiceResult<Boolean> pause(@RequestBody CsceneFlowTaskActionRequest request) {
        return flowTaskService.pause(request.getRecordId());
    }

    @ApiOperation("恢复")
    @PostMapping("/recover")
    public ServiceResult<Boolean> recover(@RequestBody CsceneFlowTaskActionRequest request) {
        return flowTaskService.unpause(request.getRecordId());
    }

    @ApiOperation("重启")
    @PostMapping("/restart")
    public ServiceResult<Boolean> restart(@RequestBody CsceneFlowTaskActionRequest request) {
        return flowTaskService.restart(request.getRecordId());
    }

    @ApiOperation("停止")
    @PostMapping("/stop")
    public ServiceResult<Boolean> stop(@RequestBody CsceneFlowTaskActionRequest request) {
        return flowTaskService.stop(request.getRecordId());
    }
}