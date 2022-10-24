package cn.cyberpeace.mp.cscene.flow;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping(ControllerConstant.BASE_API_PATH + "/flow/")
public class CsceneFlowController {
    @Resource
    private FlowBizService flowTaskService;

    @PostMapping("/start")
    public ServiceResult<Object> start(String flowTaskId, String flowGeneratorId, String targetId) {
        flowTaskService.start(flowTaskId, flowGeneratorId, targetId);
        return new ServiceResult<>();
    }
}