package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.bo.OptionModelBO;
import cn.cyberpeace.mp.common.model.OptionModel;
import cn.cyberpeace.mp.common.request.queryparam.OptionQueryParams;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import cn.cyberpeace.mp.flow.constant.*;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.*;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;
import cn.cyberpeace.mp.flow.response.AttackFlowResponse;
import cn.cyberpeace.mp.flow.response.BackgroundFlowResponse;
import cn.cyberpeace.mp.flow.response.FlowInfoResponse;
import cn.cyberpeace.mp.flow.response.PcapAnalyseResponse;
import cn.cyberpeace.mp.flow.service.FlowInfoService;
import cn.cyberpeace.mp.flow.service.FlowPcapService;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.hutool.core.io.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Api(tags = "流量管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
public class FlowInfoController extends BaseModelController<FlowInfoModel, FlowInfoQueryParams, FlowInfoService>
        implements FlowPrefixConstant {

    private static final String FLOW_CATEGORY = "flow_category";

    @Autowired
    private FlowInfoServiceImpl flowServiceImpl;

    @Autowired
    public FlowInfoController(FlowInfoService flowInfoService) {
        setModelService(flowInfoService);
    }

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @Autowired
    private FlowPcapService flowPcapService;

    
    @ApiOperation("删除流量信息")
    @ApiImplicitParam(name = "id", value = "流量ID", dataTypeClass = String.class)
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.DELETE, title = "删除流量", target = OpTargetEnums.FLOW_INFO)
    @DeleteMapping("/delete/{id}")
    public ServiceResult<Boolean> deleteFlowInfo(@PathVariable String id) {
        return flowServiceImpl.deleteFlowInfo(id, true);
    }

    
    @ApiOperation("批量删除流量信息")
    @ApiImplicitParam(name = "ids", value = "流量ID集合", dataTypeClass = String[].class)
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.DELETE, title = "删除流量", target = OpTargetEnums.FLOW_INFO)
    @DeleteMapping("/batch_delete")
    public ServiceResult<Integer> batchDeleteFlowInfo(@RequestBody String[] ids) {
        if (Objects.isNull(ids)) {
            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
        }
        return flowServiceImpl.batchDeleteFlowInfo(ids);
    }

    @GetMapping("/categories")
    @ApiOperation(value = "查询流量分类")
    public ServiceResult<List<OptionBO>> flowCategories() {
        return optionService.getIntOptions(FlowConstant.MP_COMMON_OPTION_TYPE_FLOW_CATEGORY);
    }

    
    @ApiOperation("分页查询流量信息")
    @GetMapping("/page")
    public ListObjServiceResult<FlowInfoResponse> pageFlowInfo(FlowInfoQueryParams queryParams) {
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            
            queryParams.setOrders(new String[]{"-createTime", "-title"});
        }

        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        return flowServiceImpl.pageFlowInfo(queryParams);
    }

    
    @ApiOperation("背景流量详情")
    @ApiImplicitParam(name = "id", value = "流量ID", dataTypeClass = String.class)
    @GetMapping("/background")
    public ServiceResult<BackgroundFlowResponse> queryBackgroundDetail(@RequestParam String id) {
        return flowServiceImpl.queryBackgroundDetail(id);
    }

    
    @ApiOperation("新增背景流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "新增背景流量", target = OpTargetEnums.FLOW_INFO)
    @PostMapping("/background")
    public ServiceResult<BackgroundFlowResponse> addBackgroundFlow(@Validated AddBackgroundFlowRequest addRequest) {
        return flowServiceImpl.addBackgroundFlow(addRequest);
    }

    
    @ApiOperation("更新背景流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.UPDATE, title = "更新背景流量", target = OpTargetEnums.FLOW_INFO)
    @PatchMapping("/background")
    public ServiceResult<BackgroundFlowResponse> modifyBackgroundFlow(@Validated ModifyBackgroundFlowRequest modifyRequest) {
        return flowServiceImpl.modifyBackgroundFlow(modifyRequest);
    }

    
    @ApiOperation("攻击流量详情")
    @ApiImplicitParam(name = "id", value = "流量ID", dataTypeClass = String.class)
    @GetMapping("/attack")
    public ServiceResult<AttackFlowResponse> queryAttackDetail(@RequestParam String id) {
        return flowServiceImpl.queryAttackDetail(id);
    }

    
    @ApiOperation("新增攻击流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "新增攻击流量", target = OpTargetEnums.FLOW_INFO)
    @PostMapping("/attack")
    public ServiceResult<AttackFlowResponse> addAttackFlow(@Validated AddAttackFlowRequest addRequest) {
        return flowServiceImpl.addAttackFlow(addRequest);
    }

    
    @ApiOperation("更新攻击流量")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.UPDATE, title = "更新攻击流量", target = OpTargetEnums.FLOW_INFO)
    @PatchMapping("/attack")
    public ServiceResult<AttackFlowResponse> modifyAttackFlow(@Validated ModifyAttackFlowRequest modifyRequest) {
        return flowServiceImpl.modifyAttackFlow(modifyRequest);
    }

    
    @ApiOperation("根据集合id批量查询")
    @GetMapping("/list_by_collection_ids")
    public ServiceResult<List<FlowInfoResponse>> listByCollectionIds(ListByCollectionIdsRequest request) {
        return flowServiceImpl.listByCollectionIds(request);
    }

    
    @ApiOperation("流量信息导入")
    @ApiImplicitParam(name = "request", value = "导入请求", dataTypeClass = ImportFlowInfoRequest.class)
    @PostMapping("/flow_import")
    public ServiceResult<Boolean> flowImport(@RequestBody @Validated ImportFlowInfoRequest request) {
        return flowServiceImpl.flowImport(request);
    }


    
    @ApiOperation("名称存在校验")
    @ApiImplicitParam(name = "request", value = "校验请求", dataTypeClass = CheckNamesRequest.class)
    @PostMapping("/check_names")
    public ServiceResult<List<String>> checkNames(@RequestBody @Validated CheckNamesRequest request) {
        return flowServiceImpl.checkNamesExist(request);
    }

    @ApiOperation(value = "流量包解析", notes = "获取流量包包含的协议集合")
    @PostMapping("/analyse")
    public ServiceResult<PcapAnalyseResponse> flowAnalyse(MultipartFile file) {
        PcapAnalyseResponse response = new PcapAnalyseResponse();
        if (Objects.isNull(file)) {
            throw new MpRuntimeException(FlowErrorCode.PARAMS_SHOULD_NOT_EMPTY.format("file"));
        }

        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        if (!BaseConstants.STR_PCAP.equals(suffix)) {
            throw new MpRuntimeException(FlowErrorCode.NOT_PCAP_FILE);
        }

        
        String filePath = null;
        try {
            filePath = FsCommon.upload(file.getInputStream(), suffix);
            response.setPcapFile(filePath);
        } catch (IOException e) {
            log.error("analyse upload pcap file error", e);
            return ResultCommon.fail(FlowErrorCode.FLOW_FILE_UPLOAD_ERROR);
        }

        
        List<ProtocolBO> protocolList = flowServiceImpl.pcapAnalyse(file);
        response.setProtocolList(protocolList);
        return ResultCommon.success(response);
    }

    
    @ApiOperation("分页查询流量分类列表")
    @GetMapping("/page_categories")
    public ListObjServiceResult<OptionModelBO> pageCategories(OptionQueryParams queryParams) {
        log.info("FlowInfoController.pageCategories begin. queryParams:{}", queryParams);
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            queryParams.setOrders(new String[]{"sort"});
        }
        queryParams.setType(FlowConstant.MP_COMMON_OPTION_TYPE_FLOW_CATEGORY);
        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        ListObjServiceResult<OptionModelBO> result = optionService.page(queryParams);
        log.info("FlowInfoController.pageCategories end. result:{}", result);
        return result;
    }

    
    @ApiOperation("新增流量分类")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "新增流量分类", target = OpTargetEnums.FLOW_INFO_CATEGORY)
    @PostMapping("/add_category")
    public ServiceResult addCategory(@RequestBody OptionModelBO optionModelBO) {
        log.info("FlowInfoController.addCategory begin. optionModelBO:{}", optionModelBO);
        optionModelBO.setType(FLOW_CATEGORY);
        optionModelBO.setMeta("{\"activKeys\":\"no_auth\",\"visible\":1}");
        optionModelBO.setCode(String.valueOf(optionService.getMaxCode(FLOW_CATEGORY) + 1));
        optionModelBO.setSort(optionService.getMaxSort(FLOW_CATEGORY) + 1);
        optionService.addOption(optionModelBO);
        log.info("FlowInfoController.addCategory end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("编辑流量分类")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.UPDATE, title = "编辑流量分类", target = OpTargetEnums.FLOW_INFO_CATEGORY)
    @PostMapping("/edit_category")
    public ServiceResult editCategory(@RequestBody OptionModelBO optionModelBO) {
        log.info("FlowInfoController.editCategory begin. optionModelBO:{}", optionModelBO);
        optionModelBO.setType(FLOW_CATEGORY);
        optionService.editOption(optionModelBO);
        log.info("FlowInfoController.editCategory end.");
        return ResultCommon.success();
    }

    
    @ApiOperation("删除流量分类")
    @DeleteMapping("/delete_categories")
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.DELETE, title = "删除流量分类", target = OpTargetEnums.FLOW_INFO_CATEGORY)
    public ServiceResult deleteCategories(@RequestBody DeleteCategoryRequest request){
        log.info("FlowInfoController.deleteCategories begin. request:{}", request);
        List<OptionModel> optionModels = optionService.findByIds(request.getIds());
        List<String> codeList = optionModels.stream().map(OptionModel::getCode).collect(Collectors.toList());
        if (flowServiceImpl.checkCategoryUsed(codeList)) {
            return ResultCommon.fail(FlowErrorCode.CATEGORY_HAS_USED);
        }
        optionService.deleteOptions(request.getIds());
        log.info("FlowInfoController.deleteCategories end");
        return ResultCommon.success();
    }

    
    @ApiOperation("流量分类详情")
    @ApiImplicitParam(name = "id", value = "分类id", dataTypeClass = String.class)
    @GetMapping("/categories/detail")
    public ServiceResult<OptionModelBO> findCategoriesById(@RequestParam String id){
        log.info("FlowInfoController.findCategoriesById begin. id:{}", id);
        OptionModelBO optionModelBO = optionService.findById(id);
        return new ServiceResult<>(optionModelBO).asResult();
    }

}
