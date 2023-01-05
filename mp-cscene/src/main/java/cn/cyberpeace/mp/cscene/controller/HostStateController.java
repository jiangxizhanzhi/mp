package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.util.SegmentCount2IntervalSecondsUtil;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import com.fasterxml.jackson.core.type.TypeReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:22
 */
@Api(value = "主机状态监控", tags = "主机状态监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/hoststate")
public class HostStateController {

    @Autowired
    private HostStateService hostStateService;

    /**
     * 默认查询场景所有节点
     * 忽略elementIds的值
     *
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "场景主机状态趋势图")
    @GetMapping("/scene_trend")
    public ServiceResult<List<HostState>> getSceneHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        List<HostState> hostStates = hostStateService.getSceneHostStateTrend(hostStateTrendRequest);

        return ResultCommon.success(hostStates);
    }


    /**
     * 集群状态趋势
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "集群状态趋势图")
    @GetMapping("/cluster_trend")
    public ServiceResult<List<HostState>> getClusterHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        hostStateTrendRequest.setSceneId(null);
        hostStateTrendRequest.setElementIds(null);
        List<HostState> hostStates = hostStateService.getClusterHostStateTrend(hostStateTrendRequest);

        return ResultCommon.success(hostStates);
    }


    /**
     * elementIds若为多值，则只取第一个值
     *
     * @param hostStateTrendRequest
     * @return
     */
    @ApiOperation(value = "节点主机状态趋势图")
    @GetMapping("/element_trend")
    public ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {

        if ("f5f8a55ee9274f72b7141b4f416cf21b".equals(hostStateTrendRequest.getSceneId())) {
            List<HostState> hostStates = JsonCommon.snakeParse(getElementHostStateTrendStr(), new TypeReference<List<HostState>>() {
            });
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = LocalDateTime.now().minusHours(5);
            Long intervalSeconds = SegmentCount2IntervalSecondsUtil.calculateIntervalSeconds(startTime, endTime, 10);
            List<LocalDateTime> timeList = SegmentCount2IntervalSecondsUtil.generateTimeList(startTime, endTime, intervalSeconds);
            for (int i = 0; i < 10; i++) {
                hostStates.get(i).setTime(timeList.get(i));
            }
            return ResultCommon.success(hostStates);
        }

        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);

        return ResultCommon.success(hostStates);
    }

    public static String getElementHostStateTrendStr(){
        return "[\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-05 14:31:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 0.0,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 0.0,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 0.0,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-05 17:11:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 0.0,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 0.0,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 0.0,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-05 19:51:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 31.53,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 29.55,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.42,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-05 22:31:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 30.98,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 24.37,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.39,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-06 01:11:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 31.22,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 22.42,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.4,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-06 03:51:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 31.41,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 24.68,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.4,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-06 06:31:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 31.59,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 25.86,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.39,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-06 09:11:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 31.75,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 21.9,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.4,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-06 11:51:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 32.0,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 26.89,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.41,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"scene_id\": null,\n" +
                "            \"element_id\": null,\n" +
                "            \"agent_name\": null,\n" +
                "            \"time\": \"2023-01-06 14:31:36\",\n" +
                "            \"memory_total\": null,\n" +
                "            \"memory_free\": null,\n" +
                "            \"memory_percent\": 31.95,\n" +
                "            \"cpu_freq\": null,\n" +
                "            \"cpu_percent\": 23.21,\n" +
                "            \"cpu_count\": null,\n" +
                "            \"disk_space\": null,\n" +
                "            \"disk_unused\": null,\n" +
                "            \"disk_percent\": 28.42,\n" +
                "            \"download_speed\": null,\n" +
                "            \"max_download_speed\": null,\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": null,\n" +
                "            \"max_upload_speed\": null,\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": null,\n" +
                "            \"disk_write_bytes\": null,\n" +
                "            \"disk_io_percent\": null\n" +
                "        }\n" +
                "    ]";
    }


    /**
     * 查询节点当前状态信息
     *
     * @return
     */
    @ApiOperation(value = "节点主机状态")
    @GetMapping()
    public ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest) {
        if ("f5f8a55ee9274f72b7141b4f416cf21b".equals(pageRequest.getSceneId())) {
            var host  = JsonCommon.snakeParse(getElementHostStateStr(),HostState.class);
            List<HostState> hostStates = new ArrayList<>();
            hostStates.add(host);
            return ResultCommon.success(hostStates);
        }
        pageRequest.setPage(1);
        pageRequest.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStates(pageRequest);
        return ResultCommon.success(hostStates);
    }

    public static String getElementHostStateStr(){
        return "{\n" +
                "            \"scene_id\": \"c7496a0562694f6cb9b9048d84beb98f\",\n" +
                "            \"element_id\": \"91dc9392-99db-44b6-97e8-8c30babc3d72\",\n" +
                "            \"agent_name\": \"bc52b480656f4e1587fa1acc1e606b98\",\n" +
                "            \"time\": \"2023-01-05 14:44:08\",\n" +
                "            \"memory_total\": 991,\n" +
                "            \"memory_free\": 143,\n" +
                "            \"memory_percent\": 31.8,\n" +
                "            \"cpu_freq\": 2399.0,\n" +
                "            \"cpu_percent\": 9.0,\n" +
                "            \"cpu_count\": 1,\n" +
                "            \"disk_space\": 9,\n" +
                "            \"disk_unused\": 8,\n" +
                "            \"disk_percent\": 19.82,\n" +
                "            \"download_speed\": \"0.00KB/s\",\n" +
                "            \"max_download_speed\": \"0.00KB/s\",\n" +
                "            \"download_percent\": null,\n" +
                "            \"upload_speed\": \"0.00KB/s\",\n" +
                "            \"max_upload_speed\": \"0.00KB/s\",\n" +
                "            \"upload_percent\": null,\n" +
                "            \"max_net_speed\": null,\n" +
                "            \"disk_read_bytes\": 0,\n" +
                "            \"disk_write_bytes\": 0,\n" +
                "            \"disk_io_percent\": 0.0\n" +
                "        }";
    }

    /**
     * 查询节点当前状态信息
     *
     * @return
     */
    @ApiOperation(value = "节点主机状态")
    @GetMapping("/by_agent_name")
    public ServiceResult<List<HostState>> getHostStateByAgentName(AgentNameBasedRequest request) {
        request.setPage(1);
        request.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStatesByAgentName(request);
        return ResultCommon.success(hostStates);
    }

    @ApiOperation(value = "节点端口信息")
    @GetMapping("/element_port")
    public ListObjServiceResult<Port> pagePorts(ListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pagePorts(listPageInfoQueryParams).asResult();
    }

    @ApiOperation(value = "节点进程信息")
    @GetMapping("/element_process")
    public ListObjServiceResult<Process> pageProcesses(ListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pageProcesses(listPageInfoQueryParams).asResult();
    }

    @ApiOperation(value = "节点网卡信息")
    @GetMapping("/element_netifaces")
    public ListObjServiceResult<Netiface> pageNetifaces(ListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pageNetifaces(listPageInfoQueryParams).asResult();
    }


}
