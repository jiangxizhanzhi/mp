package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.BatchDeleteFlowInfoRequest;
import cn.cyberpeace.mp.flow.request.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 流量信息控制器
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Slf4j
@Api(tags = "流量管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
public class FlowInfoController extends BaseModelController<FlowInfoModel, FlowInfoQueryParams, FlowInfoService> {

    @Autowired
    private FlowInfoServiceImpl flowServiceImpl;

    @Autowired
    public FlowInfoController(FlowInfoService flowInfoService) {
        setModelService(flowInfoService);
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

}
