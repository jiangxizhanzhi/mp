package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.*;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.AttackFlowResponse;
import cn.cyberpeace.mp.flow.response.BackgroundFlowResponse;
import cn.cyberpeace.mp.flow.response.FlowInfoResponse;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @ApiOperation("分页查询流量信息")
    @GetMapping("/page")
    public ListObjServiceResult<FlowInfoResponse> pageFlowInfo(@Validated FlowInfoQueryParams queryParams) {
        return flowServiceImpl.pageFlowInfo(queryParams);
    }

    @ApiOperation("背景流量详情")
    @GetMapping("/background_detail")
    public ServiceResult<BackgroundFlowResponse> queryBackgroundDetail(@RequestParam String id) {
        return flowServiceImpl.queryBackgroundDetail(id);
    }

    /**
     * 新增背景流量
     * @param addRequest
     * @return
     */
    @PostMapping("/background_add")
    public ServiceResult<BackgroundFlowResponse> addBackgroundFlow(@Validated AddBackgroundFlowRequest addRequest) {
        return flowServiceImpl.addBackgroundFlow(addRequest);
    }

    /**
     * 更新背景流量
     * @param modifyRequest
     * @return
     */
    @PostMapping("/background_modify")
    public ServiceResult<BackgroundFlowResponse> modifyBackgroundFlow(@Validated ModifyBackgroundFlowRequest modifyRequest) {
        return flowServiceImpl.modifyBackgroundFlow(modifyRequest);
    }

    /**
     * 攻击流量详情
     * @param id
     * @return
     */
    @ApiOperation("攻击流量详情")
    @GetMapping("/attack_detail")
    public ServiceResult<AttackFlowResponse> queryAttackDetail(@RequestParam String id) {
        return flowServiceImpl.queryAttackDetail(id);
    }

    /**
     * 新增攻击流量
     * @param addRequest
     * @return
     */
    @PostMapping("/attack_add")
    public ServiceResult<AttackFlowResponse> addAttackFlow(@Validated AddAttackFlowRequest addRequest) {
        return flowServiceImpl.addAttackFlow(addRequest);
    }

    /**
     * 更新攻击流量
     * @param modifyRequest
     * @return
     */
    @PostMapping("/attack_modify")
    public ServiceResult<AttackFlowResponse> modifyAttackFlow(@Validated ModifyAttackFlowRequest modifyRequest) {
        return flowServiceImpl.modifyAttackFlow(modifyRequest);
    }


}
