package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.devops.log.annotation.OperateLog;
import cn.cyberpeace.mp.devops.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.devops.log.enums.OpTypeEnums;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.constant.FlowConstant;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
import cn.cyberpeace.mp.flow.constant.FlowPrefixConstant;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.AddAttackFlowRequest;
import cn.cyberpeace.mp.flow.request.AddBackgroundFlowRequest;
import cn.cyberpeace.mp.flow.request.CheckNamesRequest;
import cn.cyberpeace.mp.flow.request.ImportFlowInfoRequest;
import cn.cyberpeace.mp.flow.request.ListByCollectionIdsRequest;
import cn.cyberpeace.mp.flow.request.ModifyAttackFlowRequest;
import cn.cyberpeace.mp.flow.request.ModifyBackgroundFlowRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 流量信息控制器
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Slf4j
@Api(tags = "流量管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
public class FlowInfoController extends BaseModelController<FlowInfoModel, FlowInfoQueryParams, FlowInfoService>
        implements FlowPrefixConstant {

    @Autowired
    private FlowInfoServiceImpl flowServiceImpl;

    @Autowired
    public FlowInfoController(FlowInfoService flowInfoService) {
        setModelService(flowInfoService);
    }

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    /**
     * 删除流量信息
     * @param id
     * @return
     */
    @ApiOperation("删除流量信息")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.DELETE, title = "删除流量", target = OpTargetEnums.FLOW_INFO)
    @DeleteMapping("/delete/{id}")
    public ServiceResult<Boolean> deleteFlowInfo(@PathVariable String id) {
        return flowServiceImpl.deleteFlowInfo(id, true);
    }

    /**
     * 批量删除流量信息
     * @param ids
     * @return
     */
    @ApiOperation("批量删除流量信息")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.DELETE, title = "删除流量", target = OpTargetEnums.FLOW_INFO)
    @DeleteMapping("/batch_delete")
    public ServiceResult<Integer> batchDeleteFlowInfo(@RequestBody String[] ids) {
        if (Objects.isNull(ids)) {
            return ServiceResult.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
        }
        return flowServiceImpl.batchDeleteFlowInfo(ids);
    }

    @GetMapping("/categories")
    @ApiOperation(value = "查询流量分类")
    public ServiceResult<List<OptionBO>> flowCategories() {
        return optionService.getIntOptions(FlowConstant.MP_COMMON_OPTION_TYPE_FLOW_CATEGORY);
    }

    /**
     * 分页查询流量信息
     * @param queryParams
     * @return
     */
    @ApiOperation("分页查询流量信息")
    @GetMapping("/page")
    public ListObjServiceResult<FlowInfoResponse> pageFlowInfo(FlowInfoQueryParams queryParams) {
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            // 默认按时间降序
            queryParams.setOrders(new String[]{"-createTime"});
        }

        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        return flowServiceImpl.pageFlowInfo(queryParams);
    }

    /**
     * 背景流量详情
     * @param id
     * @return
     */
    @ApiOperation("背景流量详情")
    @GetMapping("/background")
    public ServiceResult<BackgroundFlowResponse> queryBackgroundDetail(@RequestParam String id) {
        return flowServiceImpl.queryBackgroundDetail(id);
    }

    /**
     * 新增背景流量
     * @param addRequest
     * @return
     */
    @ApiOperation("新增背景流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "新增背景流量", target = OpTargetEnums.FLOW_INFO)
    @PostMapping("/background")
    public ServiceResult<BackgroundFlowResponse> addBackgroundFlow(@Validated AddBackgroundFlowRequest addRequest) {
        return flowServiceImpl.addBackgroundFlow(addRequest);
    }

    /**
     * 更新背景流量
     * @param modifyRequest
     * @return
     */
    @ApiOperation("更新背景流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.UPDATE, title = "更新背景流量", target = OpTargetEnums.FLOW_INFO)
    @PatchMapping("/background")
    public ServiceResult<BackgroundFlowResponse> modifyBackgroundFlow(@Validated ModifyBackgroundFlowRequest modifyRequest) {
        return flowServiceImpl.modifyBackgroundFlow(modifyRequest);
    }

    /**
     * 攻击流量详情
     * @param id
     * @return
     */
    @ApiOperation("攻击流量详情")
    @GetMapping("/attack")
    public ServiceResult<AttackFlowResponse> queryAttackDetail(@RequestParam String id) {
        return flowServiceImpl.queryAttackDetail(id);
    }

    /**
     * 新增攻击流量
     * @param addRequest
     * @return
     */
    @ApiOperation("新增攻击流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "新增攻击流量", target = OpTargetEnums.FLOW_INFO)
    @PostMapping("/attack")
    public ServiceResult<AttackFlowResponse> addAttackFlow(@Validated AddAttackFlowRequest addRequest) {
        return flowServiceImpl.addAttackFlow(addRequest);
    }

    /**
     * 更新攻击流量
     * @param modifyRequest
     * @return
     */
    @ApiOperation("更新攻击流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.UPDATE, title = "更新攻击流量", target = OpTargetEnums.FLOW_INFO)
    @PatchMapping("/attack")
    public ServiceResult<AttackFlowResponse> modifyAttackFlow(@Validated ModifyAttackFlowRequest modifyRequest) {
        return flowServiceImpl.modifyAttackFlow(modifyRequest);
    }

    /**
     * 根据集合id批量查询
     * @param request
     * @return
     */
    @ApiOperation("根据集合id批量查询")
    @GetMapping("/list_by_collection_ids")
    public ServiceResult<List<FlowInfoResponse>> listByCollectionIds(ListByCollectionIdsRequest request) {
        return flowServiceImpl.listByCollectionIds(request);
    }

    /**
     * 流量信息导入
     * @param request
     * @return
     */
    @ApiOperation("流量信息导入")
    @PostMapping("/flow_import")
    public ServiceResult<Boolean> flowImport(@RequestBody @Validated ImportFlowInfoRequest request) {
        return flowServiceImpl.flowImport(request);
    }


    /**
     * 名称存在校验
     * @param request
     * @return
     */
    @ApiOperation("名称存在校验")
    @PostMapping("/check_names")
    public ServiceResult<List<String>> checkNames(@RequestBody @Validated CheckNamesRequest request) {
        return flowServiceImpl.checkNamesExist(request);
    }

}
