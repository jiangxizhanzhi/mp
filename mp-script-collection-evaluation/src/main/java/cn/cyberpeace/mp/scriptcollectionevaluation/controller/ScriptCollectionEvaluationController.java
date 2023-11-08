package cn.cyberpeace.mp.scriptcollectionevaluation.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.scriptcollectionevaluation.bo.CollectionEvaluationBO;
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
import java.util.List;

@RestController
@RequestMapping("/script/collection")
@Api(value = "采集评估功能", tags = "采集评估功能")
public class ScriptCollectionEvaluationController {
    @Resource
    private ScriptCollectionEvaluationService scriptCollectionEvaluationService;

    @GetMapping("/evaluation")
    @ApiOperation(value = "根据镜像名称获取镜像采集能力评估结果")
    public ServiceResult<List<CollectionEvaluationBO>> getImageSupportCollectionEvaluation(ImageCollectionEvaluationRequest request) {
        return scriptCollectionEvaluationService.getImageSupportCollectionEvaluation(request.getName()).asResult();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除场景")
    public ServiceResult<Boolean> deleteScene(@RequestParam String sceneId) {
        return scriptCollectionEvaluationService.deleteScene(sceneId).asResult();
    }
}
