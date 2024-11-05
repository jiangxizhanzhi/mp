// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowCollectionController.java.
// File size 8655.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.scenepluginflow.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
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
import cn.cyberpeace.mp.flow.service.impl.FlowCollectionServiceImpl;
import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName FlowCollectionController
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelController
 */
@Slf4j
@Api(tags = "流量集合管理接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/collection")
public class FlowCollectionController {

    @Resource
    private FlowCollectionServiceImpl flowCollectionService;

    @ApiOperation("分页查询流量集合信息")
    @GetMapping("/page")
    public ListObjServiceResult<FlowCollectionResponse> pageFlowCollection(FlowCollectionQueryParams queryParams) {
        ListObjServiceResult<FlowCollectionModel> listResult = flowCollectionService.list(queryParams).asResult();

        List<FlowCollectionResponse> responseList = new ArrayList<>();
        for (FlowCollectionModel flowCollectionModel : listResult.getData().getList()) {
            FlowCollectionResponse collectionResponse = new FlowCollectionResponse();
            BeanUtils.copyProperties(flowCollectionModel, collectionResponse);
            // 兼容前端，冗余描述信息，和流量信息描述字段保持一致
            collectionResponse.setIntroduction(flowCollectionModel.getDescription());
            responseList.add(collectionResponse);
        }
        return new ListObjServiceResult<>(new ListObjResult<>(responseList, listResult.getSource()
                .getTotal())).asResult();
    }

    /**
     * get
     * @param id
     * @return
     */
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

    /**
     * add
     * @param request
     * @return
     */
    @ApiOperation("新增流量集合信息")
    @ApiImplicitParam(name = "request", value = "新增集合请求", dataTypeClass = AddFlowCollectionRequest.class)
    @PostMapping("/bulk")
    public ServiceResult<FlowCollectionModel> add(@RequestBody @Validated AddFlowCollectionRequest request) {
        return flowCollectionService.addCollection(request);
    }

    /**
     * update
     * @param request
     * @return
     */
    @ApiOperation("更新流量集合信息")
    @ApiImplicitParam(name = "request", value = "更新集合请求", dataTypeClass = ModifyCollectionRequest.class)
    @PatchMapping("/bulk")
    public ServiceResult<FlowCollectionModel> update(@RequestBody @Validated ModifyCollectionRequest request) {
        return flowCollectionService.updateCollection(request);
    }

    /**
     * getByCollectionId
     * @param queryParams
     * @return
     */
    @ApiOperation("根据集合id查询流量信息")
    @GetMapping("/detail/page")
    public ListObjServiceResult<FlowInfoModel> getByCollectionId(FlowCollectionRelQueryParams queryParams) {
        if (StringUtils.isBlank(queryParams.getCollectionId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }

        if (Objects.isNull(queryParams.getOrders()) && Objects.isNull(queryParams.getSort())) {
            // 默认按时间降序
            queryParams.setOrders(new String[]{"-createTime"});
        }
        return flowCollectionService.getByCollectionId(queryParams);
    }

    /**
     * addCollectionRel
     * @param request
     * @return
     */
    @ApiOperation("新增集合关联信息")
    @ApiImplicitParam(name = "request", value = "新增关联信息请求", dataTypeClass = AddFlowCollectionRelRequest.class)
    @PostMapping("/collection_rel")
    public ServiceResult<Integer> addCollectionRel(@RequestBody @Validated AddFlowCollectionRelRequest request) {
        return flowCollectionService.addCollectionRel(request);
    }

    /**
     * delCollectionRel
     * @param request
     * @return
     */
    @ApiOperation("删除集合关联信息")
    @ApiImplicitParam(name = "request", value = "删除请求体", dataTypeClass = DelFlowCollectionRelRequest.class)
    @DeleteMapping("/collection_rel")
    public ServiceResult<Integer> delCollectionRel(@RequestBody @Validated DelFlowCollectionRelRequest request) {
        return flowCollectionService.delCollectionRel(request);
    }

}
