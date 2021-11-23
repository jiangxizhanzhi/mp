package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.request.FlowAnalysisRequest;
import cn.cyberpeace.mp.situationaware.service.FlowAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 流量分析Controller
 * @author wanggang
 */
@Slf4j
@RestController
@RequestMapping("/flow/analysis")
@Validated
@Api(tags = "流量分析")
public class FlowAnalysisController {

    private FlowAnalysisService flowAnalysisService;


    @Autowired
    public FlowAnalysisController(FlowAnalysisService flowAnalysisService) {
        this.flowAnalysisService = flowAnalysisService;
    }

    @ApiOperation("初始化攻击分类")
    @GetMapping("/init-classification")
    public void initClassification() throws IOException {
        //初始化分类数据
        flowAnalysisService.initClassification();
    }

    @ApiOperation("测试")
    @GetMapping("/test")
    public ServiceResult updateDate(FlowAnalysisRequest request){
        flowAnalysisService.flowLog(request);
        return new ServiceResult().asResult();
    }
}
