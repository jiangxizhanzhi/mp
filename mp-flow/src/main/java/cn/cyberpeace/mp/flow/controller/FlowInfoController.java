// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowInfoController.java.
// File size 15999.
// File create date Fri Jul 12 12:55:30 CST 2024.
// File update date Fri Jul 12 12:55:30 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.FsCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.flow.bo.ProtocolBO;
import cn.cyberpeace.mp.flow.constant.BaseConstants;
import cn.cyberpeace.mp.flow.constant.ControllerConstant;
import cn.cyberpeace.mp.flow.constant.FlowErrorCode;
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
import cn.cyberpeace.mp.flow.response.PcapAnalyseResponse;
import cn.cyberpeace.mp.flow.service.impl.FlowInfoServiceImpl;
import cn.cyberpeace.mp.flow.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Api(tags = "流量资源管理")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/info")
public class FlowInfoController {

    @Autowired
    private FlowInfoServiceImpl flowServiceImpl;

    /**
     * 删除流量信息
     * @param id
     * @return
     */
    @ApiOperation("删除流量信息")
    @ApiImplicitParam(name = "id", value = "流量ID", dataTypeClass = String.class)
    @DeleteMapping("/delete")
    public ServiceResult<Boolean> deleteFlowInfo(@RequestParam String id) {
        return flowServiceImpl.deleteFlowInfo(id, true).asResult();
    }

    /**
     * 批量删除流量信息
     * @param ids
     * @return
     */
    @AuthTarget({TargetEnum.DYNAMIC_TRAFFIC_LIST, TargetEnum.STATIC_TRAFFIC_LIST})
    @ApiOperation("批量删除流量信息")
    @ApiImplicitParam(name = "ids", value = "流量ID集合", dataTypeClass = String[].class)
    @DeleteMapping("/batch_delete")
    public ServiceResult<Integer> batchDeleteFlowInfo(@RequestBody String[] ids) {
        if (Objects.isNull(ids)) {
            return ResultCommon.fail(FlowErrorCode.IDS_CAN_NOT_EMPTY);
        }
        return flowServiceImpl.batchDeleteFlowInfo(ids).asResult();
    }

    @GetMapping("/protocol")
    @ApiOperation(value = "流量协议列表")
    public ServiceResult<List<OptionBO<Integer>>> listFlowProtocol() {
        List<String> protocols = flowServiceImpl.listFlowProtocols();
        List<OptionBO<Integer>> optionBOS = new ArrayList<>();
        for (int i = 0; i < protocols.size(); i++) {
            OptionBO<Integer> optionBO = new OptionBO<>();
            optionBO.setCode(i);
            optionBO.setName(protocols.get(i));
            optionBOS.add(optionBO);
        }
        return new ServiceResult<>(optionBOS).asResult();
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
            queryParams.setOrders(new String[]{"-createTime", "-title"});
        }

        return flowServiceImpl.pageFlowInfo(queryParams);
    }

    /**
     * 背景流量详情
     * @param id
     * @return
     */
    @ApiOperation("背景流量详情")
    @ApiImplicitParam(name = "id", value = "流量ID", dataTypeClass = String.class)
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
    @PostMapping("/background")
    public ServiceResult<BackgroundFlowResponse> addBackgroundFlow(@Validated AddBackgroundFlowRequest addRequest) {
        return flowServiceImpl.addBackgroundFlow(addRequest);
    }

    /**
     * 更新背景流量
     * @param modifyRequest
     * @return
     */
    @AuthTarget(TargetEnum.STATIC_TRAFFIC_LIST)
    @ApiOperation("更新背景流量")
    @PatchMapping("/background")
    public ServiceResult<BackgroundFlowResponse> modifyBackgroundFlow(@Validated ModifyBackgroundFlowRequest modifyRequest) {
        return flowServiceImpl.modifyBackgroundFlow(modifyRequest);
    }

    /**
     * 攻击流量详情
     * @param id
     * @return
     */
    @AuthTarget(TargetEnum.DYNAMIC_TRAFFIC_LIST)
    @ApiOperation("攻击流量详情")
    @ApiImplicitParam(name = "id", value = "流量ID", dataTypeClass = String.class)
    @GetMapping("/attack")
    public ServiceResult<AttackFlowResponse> queryAttackDetail(@RequestParam String id) {
        return flowServiceImpl.queryAttackDetail(id);
    }

    /**
     * 新增攻击流量
     * @param addRequest
     * @return
     */
    @AuthTarget(TargetEnum.DYNAMIC_TRAFFIC_LIST)
    @ApiOperation("新增攻击流量")
    @PostMapping("/attack")
    public ServiceResult<AttackFlowResponse> addAttackFlow(@Validated AddAttackFlowRequest addRequest) {
        return flowServiceImpl.addAttackFlow(addRequest);
    }

    /**
     * 更新攻击流量
     * @param modifyRequest
     * @return
     */
    @AuthTarget(TargetEnum.DYNAMIC_TRAFFIC_LIST)
    @ApiOperation("更新攻击流量")
    @PatchMapping("/attack")
    public ServiceResult<AttackFlowResponse> modifyAttackFlow(@Validated ModifyAttackFlowRequest modifyRequest) {
        return flowServiceImpl.modifyAttackFlow(modifyRequest);
    }

    /**
     * 根据集合id批量查询
     * @param request
     * @return
     */
    @AuthTarget(TargetEnum.FLOW_GROUP)
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
    @ApiImplicitParam(name = "request", value = "导入请求", dataTypeClass = ImportFlowInfoRequest.class)
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
    @ApiImplicitParam(name = "request", value = "校验请求", dataTypeClass = CheckNamesRequest.class)
    @PostMapping("/check_names")
    public ServiceResult<List<String>> checkNames(@RequestBody @Validated CheckNamesRequest request) {
        return flowServiceImpl.checkNamesExist(request);
    }

    /**
     * flowAnalyse
     * @param file
     * @return
     */
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

        // 上传文件获取路径
        String filePath = null;
        try {
            filePath = FsCommon.upload(file.getInputStream(), suffix);
            response.setPcapFile(filePath);
        } catch (IOException e) {
            log.error("analyse upload pcap file error", e);
            return ResultCommon.fail(FlowErrorCode.FLOW_FILE_UPLOAD_ERROR);
        }

        // 解析流量包包含的协议内容
        List<ProtocolBO> protocolList = flowServiceImpl.pcapAnalyse(file);
        response.setProtocolList(protocolList);
        return ResultCommon.success(response);
    }

}
