package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.AddFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.AddFlowCollectionRequest;
import cn.cyberpeace.mp.flow.request.DelFlowCollectionRelRequest;
import cn.cyberpeace.mp.flow.request.ModifyCollectionRequest;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionQueryParams;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionRelQueryParams;
import cn.cyberpeace.mp.flow.response.FlowCollectionResponse;
import cn.cyberpeace.mp.flow.service.FlowCollectionService;
import cn.cyberpeace.mp.flow.service.impl.FlowCollectionServiceImpl;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Api(tags = "流量集合管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/collection")
public class FlowCollectionController extends BaseModelController<FlowCollectionModel, FlowCollectionQueryParams, FlowCollectionService> {

    @Autowired
    public FlowCollectionController(FlowCollectionService FlowCollectionService) {
        setModelService(FlowCollectionService);
    }

    @Autowired
    private FlowCollectionServiceImpl flowCollectionService;

    
    @ApiOperation("分页查询流量集合信息")
    @GetMapping("/page")
    public ListObjServiceResult<FlowCollectionResponse> pageFlowCollection(FlowCollectionQueryParams queryParams) {
        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            
            queryParams.setOrders(new String[]{"-createTime"});
        }

        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        ListObjServiceResult<FlowCollectionModel> listResult = getModelService().list(queryParams).asResult();

        List<FlowCollectionResponse> responseList = new ArrayList<>();
        listResult.getData().getList().forEach(flowCollectionModel -> {
            FlowCollectionResponse collectionResponse = new FlowCollectionResponse();
            BeanUtils.copyProperties(flowCollectionModel, collectionResponse);
            
            collectionResponse.setIntroduction(flowCollectionModel.getDescription());
            responseList.add(collectionResponse);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(responseList, listResult.getSource().getTotal())).asResult();
    }

    @ApiOperation("查询指定id数据")
    @ApiImplicitParam(name = "id", value = "集合ID", dataTypeClass = String.class)
    @GetMapping("/bulk")
    public ServiceResult<FlowCollectionResponse> get(@RequestParam String id) {
        FlowCollectionResponse response = new FlowCollectionResponse();
        FlowCollectionModel model = new FlowCollectionModel();
        ObjServiceResult<FlowCollectionModel> result = flowCollectionService.get(id);
        if (Objects.nonNull(result.getSource())) {
            model = result.getSource().asResult();
            BeanUtils.copyProperties(model, response);
        }

        FlowCollectionRelQueryParams queryParams = new FlowCollectionRelQueryParams();
        queryParams.setCollectionId(id);
        List<FlowInfoModel> flowInfoModels = flowCollectionService.getFlowsByCollectionId(queryParams);
        response.setFlowInfoList(flowInfoModels);
        return ResultCommon.success(response);
    }

    @ApiOperation("新增流量集合信息")
    @ApiImplicitParam(name = "request", value = "新增集合请求", dataTypeClass = AddFlowCollectionRequest.class)
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "新增流量集合", target = OpTargetEnums.FLOW_COLLECTION)
    @PostMapping("/bulk")
    public ServiceResult<FlowCollectionModel> add(@RequestBody @Validated AddFlowCollectionRequest request) {
        return flowCollectionService.addCollection(request);
    }

    @ApiOperation("更新流量集合信息")
    @ApiImplicitParam(name = "request", value = "更新集合请求", dataTypeClass = ModifyCollectionRequest.class)
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "更新流量集合", target = OpTargetEnums.FLOW_COLLECTION)
    @PatchMapping("/bulk")
    public ServiceResult<FlowCollectionModel> update(@RequestBody @Validated ModifyCollectionRequest request) {
        return flowCollectionService.updateCollection(request);
    }

    @ApiOperation("根据集合id查询流量信息")
    @GetMapping("/detail/page")
    public ListObjServiceResult<FlowInfoModel> getByCollectionId(FlowCollectionRelQueryParams queryParams) {
        if (StringUtils.isBlank(queryParams.getCollectionId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            
            queryParams.setOrders(new String[]{"-createTime"});
        }
        handleQueryParams(BaseConstants.STR_LIST, queryParams);
        return flowCollectionService.getByCollectionId(queryParams);
    }

    @ApiOperation("新增集合关联信息")
    @ApiImplicitParam(name = "request", value = "新增关联信息请求", dataTypeClass = AddFlowCollectionRelRequest.class)
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.CREATE, title = "新增流量管理", target = OpTargetEnums.FLOW_COLLECTION)
    @PostMapping("/collection_rel")
    public ServiceResult<Integer> addCollectionRel(@RequestBody @Validated AddFlowCollectionRelRequest request) {
        return flowCollectionService.addCollectionRel(request);
    }

    @ApiOperation("删除集合关联信息")
    @ApiImplicitParam(name = "request", value = "删除请求体", dataTypeClass = DelFlowCollectionRelRequest.class)
    @OperateLog(module = OpModuleEnums.FLOW, type = OpTypeEnums.UPDATE, title = "更新流量管理", target = OpTargetEnums.FLOW_COLLECTION)
    @DeleteMapping("/collection_rel")
    public ServiceResult<Integer> delCollectionRel(@RequestBody @Validated DelFlowCollectionRelRequest request) {
        return flowCollectionService.delCollectionRel(request);
    }

}
