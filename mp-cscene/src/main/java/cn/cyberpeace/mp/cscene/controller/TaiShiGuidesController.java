 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TaiShiGuidesController.java.
 // File size 4428.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackTypeCount;
import cn.cyberpeace.mp.cscene.dto.flow.FlowClassificationDto;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.scene.TaiShiEventInfo;
import cn.cyberpeace.mp.cscene.dto.scene.TaiShiTotalCount;
import cn.cyberpeace.mp.cscene.request.common.TaiShiHostRequest;
import cn.cyberpeace.mp.cscene.request.common.TaiShiRequest;
import cn.cyberpeace.mp.cscene.service.TaiShiGuidesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName TaiShiGuidesController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Api(value = "态势导调", tags = "CSCENE-态势导调")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/taishi")
@Data
public class TaiShiGuidesController {

    @Resource
    private TaiShiGuidesService taiShiGuidesService;

    /**
     * getAttCkEventList
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "场景态势事件列表")
    @PostMapping("/event_list")
    public ListObjServiceResult<TaiShiEventInfo> getAttCkEventList(@RequestBody TaiShiRequest request) {
        log.info("getAttackEventList request = {}", request);
        return taiShiGuidesService.getAttCkEventList(request).asResult();
    }

    /**
     * getHostEventList
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "场景态势主机事件列表")
    @GetMapping("/host_event_list")
    public ListObjServiceResult<TaiShiEventInfo> getHostEventList(TaiShiHostRequest request) {
        return taiShiGuidesService.getHostEventList(request).asResult();
    }

    /**
     * getAttCkEventCount
     *
     * @param sceneId
     * @return
     */
    @ApiOperation(value = "场景态势主机和流量的次数统计")
    @GetMapping("/event_total_count")
    public ServiceResult<TaiShiTotalCount> getAttCkEventCount(@Param("sceneId") String sceneId) {
        return taiShiGuidesService.getAttCkEventCount(sceneId).asResult();
    }

    /**
     * getAttCkEventGroupCount
     *
     * @param sceneId
     * @return
     */
    @ApiOperation(value = "场景态势主机和流量的次数统计")
    @GetMapping("/event_group_count")
    public ServiceResult<List<FlowAttackTypeCount>> getAttCkEventGroupCount(@Param("sceneId") String sceneId) {
        return taiShiGuidesService.getAttCkEventGroupCount(sceneId).asResult();
    }

    /**
     * getEventTrend
     *
     * @param sceneId
     * @return
     */
    @ApiOperation(value = "场景态势主机和流量事件的次数趋势")
    @GetMapping("/event_trend")
    public ServiceResult<List<FlowTrend>> getEventTrend(@Param("sceneId") String sceneId) {
        return taiShiGuidesService.getEventTrend(sceneId).asResult();
    }

    /**
     * getEventTiming
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "场景运行过程中态势主机和流量事件的出现时间")
    @RequestMapping("/event_timing")
    public ServiceResult<List<FlowTrend>> getEventTiming(@RequestBody TaiShiRequest request) {
        return taiShiGuidesService.getEventTiming(request).asResult();
    }

    /**
     * listAllFlowClassifications
     *
     * @return
     */
    @ApiOperation(value = "获取流量事件攻击类型分类详情")
    @GetMapping("/flow_classification/all")
    public ServiceResult<List<FlowClassificationDto>> listAllFlowClassifications() {
        return new ServiceResult<>(taiShiGuidesService.listAllFlowClassifications()).asResult();
    }



}