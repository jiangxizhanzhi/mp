package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.event.EventAggr;
import cn.cyberpeace.mp.cscene.dto.event.EventTimeCount;
import cn.cyberpeace.mp.cscene.request.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.request.event.EventPercentRequest;
import cn.cyberpeace.mp.cscene.request.event.EventTrendRequest;
import cn.cyberpeace.mp.cscene.service.EventMonitorService;
import cn.cyberpeace.mp.cscene.service.SceneJsonMockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:22
 */
@Api(value = "事件监控", tags = "事件监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/event")
public class EventMonitorController {

    @Autowired
    private EventMonitorService eventMonitorService;

    @Autowired
    private SceneJsonMockService sceneJsonMockService;

    /**
     * 查询事件总数
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询一定时间范围内的事件总数")
    @GetMapping("/sum")
    public ServiceResult<Long> getAllEventSum(BaseTimeRangeRequest baseRequest) {
        Long sum = eventMonitorService.sumEvent(baseRequest);
        return ResultCommon.success(sum);
    }


    /**
     * 采集事件统计，查询各个节点事件的占比情况
     * 默认排序为前5个节点
     * @param eventPercentRequest
     * @return
     */
    @ApiOperation(value = "根据事件占比多少，统计并排序节点，默认显示前5个节点")
    @GetMapping("/aggr_top")
    public ServiceResult<List<ElementEventPercent>> getTopElementsByEventCount(EventPercentRequest eventPercentRequest) {
        //List<ElementEventPercent> eventPercents = eventMonitorService.topElementByEventPercent(eventPercentRequest);
        //
        //if (Objects.isNull(eventPercentRequest.getTopCount()) || eventPercentRequest.getTopCount() < 0) {
        //    eventPercentRequest.setTopCount(5);
        //}
        //List<ElementEventPercent> limitList = eventPercents.stream().limit(eventPercentRequest.getTopCount()).collect(Collectors.toList());
        var limitList = sceneJsonMockService.getEventAggrTop();
        return ResultCommon.success(limitList);
    }


    /**
     * 根据事件类型，统计饼图
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "根据事件类型，统计饼图")
    @GetMapping("/aggr")
    public ServiceResult<EventAggr> getEventAggr(BaseTimeRangeRequest baseRequest) {
        //EventAggr eventAggr = eventMonitorService.aggrByEventType(baseRequest);
        var eventAggr = sceneJsonMockService.getEventAggr();
        return ResultCommon.success(eventAggr);
    }

    /**
     * 采集事件趋势图
     *
     * @param
     * @return
     */
    @ApiOperation(value = "采集事件趋势图")
    @GetMapping("/event_trend")
    public ServiceResult<List<EventTimeCount>> getEventTrend(EventTrendRequest eventTrendRequest) {
        List<EventTimeCount> eventTimeCounts = eventMonitorService.getEventTrend(eventTrendRequest);
        return ResultCommon.success(eventTimeCounts);
    }

}
