package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.event.ElementEventPercent;
import cn.cyberpeace.mp.cscene.dto.event.EventAggr;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
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

    /**
     * 忽略elementIds
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "根据事件占比多少，统计节点")
    @GetMapping("/aggr_top")
    public ServiceResult<List<ElementEventPercent>> getTopElementsByEventCount(BaseSceneElementRequest baseRequest) {
        log.info("{}", baseRequest);
        List<ElementEventPercent> eventPercents = mockElementPercents();
        return ServiceResult.success(eventPercents);
    }


    /**
     * 查询场景所有事件统计，或者单个节点的事件统计（elementIds为多值，则只取第一个值）
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "场景采集事件统计, elementId为空表示查询场景所有事件统计")
    @GetMapping("/aggr")
    public ServiceResult<EventAggr> getEventAggr(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);

        EventAggr eventAggr = mockEventAggr();
        return ServiceResult.success(eventAggr);
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
