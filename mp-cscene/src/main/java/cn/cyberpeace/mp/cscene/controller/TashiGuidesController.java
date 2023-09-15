package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.flow.FlowTrend;
import cn.cyberpeace.mp.cscene.dto.scene.TaiShiEventInfo;
import cn.cyberpeace.mp.cscene.dto.scene.TaiShiTotalCount;
import cn.cyberpeace.mp.cscene.request.common.TaiShiRequest;
import cn.cyberpeace.mp.cscene.service.TaiShiGuidesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "态势导调", tags = "CSCENE-态势导调")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/taishi")
@Data
public class TashiGuidesController {

    @Resource
    private TaiShiGuidesService taiShiGuidesService;

    @ApiOperation(value = "场景态势事件列表")
    @GetMapping("/event_list")
    public ListObjServiceResult<TaiShiEventInfo> getAttCkEventList(TaiShiRequest request) {
        return taiShiGuidesService.getAttCkEventList(request).asResult();
    }

    @ApiOperation(value = "场景态势主机和流量的次数统计")
    @GetMapping("/event_total_count")
    public ServiceResult<TaiShiTotalCount> getAttCkEventCount(@Param("sceneId") String sceneId) {
        return taiShiGuidesService.getAttCkEventCount(sceneId).asResult();
    }

    @ApiOperation(value = "场景态势主机和流量事件的次数趋势")
    @GetMapping("/event_trend")
    public ServiceResult<List<FlowTrend>> getEventTrend(@Param("sceneId") String sceneId) {
        return taiShiGuidesService.getEventTrend(sceneId).asResult();
    }

}