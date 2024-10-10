package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.request.logset.LogSetRequest;
import cn.cyberpeace.mp.cscene.service.service.CsceneLogSetImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "场景日志设置", tags = "场景日志设置")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/log/set")
public class CsceneLogSetController {

    @Autowired
    private CsceneLogSetImpl csceneLogSetImpl;

    /*
     * 修改场景采集数据周期
     * */
    @ApiOperation(value = "修改场景采集数据周期", notes = "")
    @PostMapping("/modifySceneCollectionDataCycle")
    public ServiceResult<Boolean> modifySceneCollectionDataCycle(@RequestBody LogSetRequest requestData) {
        Boolean result = csceneLogSetImpl.modifySceneCollectionDataCycle(requestData);
        return new ServiceResult<Boolean>(result, BaseErrorCode.SUCCESS).asResult();
    }

    /*
     * 获取场景采集数据周期
     * */
    @ApiOperation(value = "获取场景采集数据周期", notes = "")
    @GetMapping("/getSceneCollectionDataCycle")
    public ServiceResult<Integer> getSceneCollectionDataCycle(@RequestParam(value = "scene_id") String sceneId) {
        Integer days = csceneLogSetImpl.getSceneCollectionDataCycle(sceneId);
        return new ServiceResult<Integer>(days, BaseErrorCode.SUCCESS).asResult();
    }

}
