package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.request.OpenflowActionRequest;
import cn.cyberpeace.mp.devops.cluster.response.OpenflowActionResponse;
import cn.cyberpeace.mp.devops.cluster.service.impl.OpenFlowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 流表监控类
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/openflow")
@Api(tags = "流表监控")
public class OpenFlowController {

    @Autowired
    private OpenFlowService openFlowService;

    /**
     * 流表操作查询
     * @param method
     * @param hostname
     * @return
     */
    @ApiOperation("流表操作查询")
    @GetMapping("/action")
    public ServiceResult<OpenflowActionResponse> queryOpenFlowAction(@RequestParam String method, @RequestParam String hostname) {
        OpenflowActionRequest request = OpenflowActionRequest.builder()
                .method(method)
                .hostname(hostname)
                .build();
        OpenflowActionResponse response = openFlowService.queryOpenFlowAction(request);

        return ServiceResult.success(response);
    }

}
