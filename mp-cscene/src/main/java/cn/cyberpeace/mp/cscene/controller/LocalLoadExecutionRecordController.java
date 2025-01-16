package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.model.LocalLoadExecutionRecordModel;
import cn.cyberpeace.mp.cscene.request.queryparam.ScreenshotQueryParams;
import cn.cyberpeace.mp.cscene.service.LocalLoadExecutionRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(value = "本地载荷记录", tags = "CSCENE-本地载荷记录")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/local_load")
public class LocalLoadExecutionRecordController extends BaseModelController<LocalLoadExecutionRecordModel, ScreenshotQueryParams, LocalLoadExecutionRecordService> {

    @Autowired
    public LocalLoadExecutionRecordController(LocalLoadExecutionRecordService localLoadExecutionRecordService) {
        setModelService(localLoadExecutionRecordService);
    }

    //    根据状态处理本地载荷执行记录，并根据流量数据更新记录状态
    @RequestMapping("/processExecutionRecords")
    public ServiceResult<List<LocalLoadExecutionRecordModel>> processExecutionRecords() {
        List<LocalLoadExecutionRecordModel> list = getModelService().processExecutionRecordsByStatus();
        return new ServiceResult<>(list).asResult();
    }

    //    查记录用于演示
    @RequestMapping("/demoExecutionRecords")
    public ServiceResult<List<LocalLoadExecutionRecordModel>> getDemoExecutionRecords() {
        List<LocalLoadExecutionRecordModel> list = getModelService().getDemoExecutionRecords();
        return new ServiceResult<>(list).asResult();
    }

    @ApiOperation(value = "根据id获取执行记录")
    @RequestMapping("/getExecutionRecord")
    public ServiceResult<LocalLoadExecutionRecordModel> getExecutionRecord(@RequestParam(value = "id") String id) {
        LocalLoadExecutionRecordModel model = getModelService().getExecutionRecord(id);
        return new ServiceResult<>(model).asResult();
    }
}
