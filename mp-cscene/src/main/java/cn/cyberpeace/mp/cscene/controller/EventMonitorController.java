package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseTimeRangeRequest;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.event.EventAggr;
import cn.cyberpeace.mp.cscene.dto.event.EventTimeCount;
import cn.cyberpeace.mp.cscene.dto.request.EventPercentRequest;
import cn.cyberpeace.mp.cscene.dto.request.EventTrendRequest;
import cn.cyberpeace.mp.cscene.service.EventMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    /**
     * 查询事件总数
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询一定时间范围内的事件总数")
    @GetMapping("/sum")
    public ServiceResult<Long> getAllEventSum(BaseTimeRangeRequest baseRequest) {
        log.info("{}", baseRequest);
        Long sum = eventMonitorService.sumEvent(baseRequest);
        return ServiceResult.success(sum);
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
        log.info("{}", eventPercentRequest);

        List<ElementEventPercent> eventPercents = eventMonitorService.topElementByEventPercent(eventPercentRequest);

        if (Objects.isNull(eventPercentRequest.getTopCount()) || eventPercentRequest.getTopCount() < 0) {
            eventPercentRequest.setTopCount(5);
        }
        List<ElementEventPercent> limitList = eventPercents.stream().limit(eventPercentRequest.getTopCount()).collect(Collectors.toList());
//        List<ElementEventPercent> eventPercents = mockElementPercents();
        return ServiceResult.success(limitList);
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
        log.info("baseRequest:{}", baseRequest);
        // TODO 暂时还是测试数据
        EventAggr eventAggr = eventMonitorService.aggrByEventType(baseRequest);
//        EventAggr eventAggr = mockEventAggr();

        return ServiceResult.success(eventAggr);
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

        return ServiceResult.success(eventTimeCounts);
    }


    private EventAggr mockEventAggr() {
        EventAggr eventAggr = new EventAggr();
        eventAggr.setProcess(23.81);
        eventAggr.setFile(22.62);
        eventAggr.setPort(19.05);
        eventAggr.setNetwork(17.86);
        eventAggr.setUser(16.67);
        return eventAggr;
    }


    private List<ElementEventPercent> mockElementPercents() {
        List<ElementEventPercent> list = new ArrayList();

        ElementEventPercent e1 = new ElementEventPercent();
        e1.setElementId("1");
        e1.setElementName("node1");
        e1.setPercent(88.0);


        ElementEventPercent e2 = new ElementEventPercent();
        e2.setElementId("2");
        e2.setElementName("node2");
        e2.setPercent(80.0);

        ElementEventPercent e3 = new ElementEventPercent();
        e3.setElementId("3");
        e3.setElementName("node3");
        e3.setPercent(48.0);

        ElementEventPercent e4 = new ElementEventPercent();
        e4.setElementId("4");
        e4.setElementName("node4");
        e4.setPercent(37.0);

        ElementEventPercent e5 = new ElementEventPercent();
        e5.setElementId("5");
        e5.setElementName("node5");
        e5.setPercent(25.0);
        Collections.addAll(list, e1, e2, e3, e4, e5);
        return list;
    }


}
