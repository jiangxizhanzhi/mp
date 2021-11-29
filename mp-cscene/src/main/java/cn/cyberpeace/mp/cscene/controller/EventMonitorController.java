package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.event.EventPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    @ApiOperation(value = "采集事件分类")
    @GetMapping("/aggr")
    public ServiceResult getEventAggregation() {
        return ServiceResult.success(mockEventAggr());
    }

    @ApiOperation(value = "分页查询事件")
    @GetMapping("/page")
    public ServiceResult getEventPage(EventPageRequest pageRequest) {
        log.info("pageRequest:{}", pageRequest);

        return null;
    }


    private Map<String, String> mockEventAggr() {
        Map<String, String> aggrmap = new HashMap<>();
        aggrmap.put("进程", "40%");
        aggrmap.put("文件", "20%");
        aggrmap.put("端口", "15%");
        aggrmap.put("网络连接", "15%");
        aggrmap.put("用户", "10%");
        return aggrmap;
    }


}
