package cn.cyberpeace.mp.scriptcollectionevaluation.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scriptcollectionevaluation.bo.CollectionEvaluationResult;
import cn.cyberpeace.mp.scriptcollectionevaluation.request.ImageCollectionEvaluationRequest;
import cn.cyberpeace.mp.scriptcollectionevaluation.service.ScriptCollectionEvaluationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/script/collection_evaluation")
@Api(value = "采集评估功能", tags = "采集评估功能")
public class ScriptCollectionEvaluationController {
    @Resource
    private ScriptCollectionEvaluationService scriptCollectionEvaluationService;

    @GetMapping("/start")
    @ApiOperation(value = "开始镜像采集能力评估")
    public ServiceResult<Boolean> startImageCollectionEvaluation(ImageCollectionEvaluationRequest request) {
        return scriptCollectionEvaluationService.startImageCollectionEvaluation(request).asResult();
    }

    @GetMapping("")
    @ApiOperation(value = "获取镜像采集能力评估结果")
    public ServiceResult<CollectionEvaluationResult> getImageCollectionEvaluationResult(@RequestParam String name) {
        return scriptCollectionEvaluationService.getImageCollectionEvaluationResult(name).asResult();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除镜像采集能力评估场景")
    public ServiceResult<Boolean> deleteScene(@RequestParam String name) {
        return scriptCollectionEvaluationService.deleteScene(name).asResult();
    }
}
