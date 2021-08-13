package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowInfoRequest;
import cn.cyberpeace.mp.flow.request.FlowExecuteRequest;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.response.FlowInfoUploadResponse;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.impl.FlowExecuteScheduleMethodImpl;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.flow.util.FlowExecuteScheduleUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Api(tags = "流量管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
public class FlowInfoController extends BaseModelController<FlowInfoModel, QueryParams, FlowInfoService> {

    @Autowired
    private FlowExecuteScheduleMethodImpl flowExecuteScheduleMethod;
    @Autowired
    private FlowInfoServiceImpl flowServiceImpl;

    @Autowired
    public FlowInfoController(FlowInfoService flowInfoService) {
        setModelService(flowInfoService);
    }


    /**
     * 上传流量文件
     * @param resourceType
     * @param file
     * @param request
     * @return
     */
    @ApiOperation("上传流量文件")
    @PostMapping("/upload")
    public ServiceResult<FlowInfoUploadResponse> uploadFlowInfo(@RequestParam int resourceType, MultipartFile file,
                                                                HttpServletRequest request) {
        return flowServiceImpl.uploadFlowInfo(resourceType, file, request);
    }

    /**
     * 更新流量信息
     * @param flowModel
     * @return
     */
    @ApiOperation("更新流量信息")
    @PatchMapping("/update")
    public ServiceResult<FlowInfoModel> updateFlowInfo(@RequestBody @Validated FlowInfoModel flowModel) {
        return flowServiceImpl.updateFlowInfo(flowModel);
    }

    /**
     * 删除流量信息
     * @param id
     * @return
     */
    @ApiOperation("删除流量信息")
    @DeleteMapping("/delete/{id}")
    public ServiceResult<Boolean> deleteFlowInfo(@PathVariable String id) {
        return flowServiceImpl.deleteFlowInfo(id);
    }

    /**
     * 批量删除流量信息
     * @param request
     * @return
     */
    @ApiOperation("批量删除流量信息")
    @DeleteMapping("/batch_delete")
    public ServiceResult<Integer> batchDeleteFlowInfo(@Validated BatchDeleteFlowInfoRequest request) {
        return flowServiceImpl.batchDeleteFlowInfo(request);
    }

    /**
     * 定时执行流量发生任务
     *
     * @param request
     * @return
     */
    @ApiOperation("定时执行流量发生任务")
    @PostMapping("/execute")
    public ServiceResult flowExecuteSchedule(@RequestBody @Validated FlowExecuteRequest request) {
        FlowExecuteScheduleUtil util = FlowExecuteScheduleUtil.builder().build();
        util.setFlowBaseModel(request);
        util.setMethod(flowExecuteScheduleMethod);
        util.schedule();

        return ServiceResult.successful();
    }

}
