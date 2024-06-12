package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.dto.element.*;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.cscene.util.FileUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

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
        if (StringUtils.isEmpty(hostStateTrendRequest.getSceneId())) {
            return ResultCommon.success(new ArrayList<>());
        }
        if (CollectionUtils.isNotEmpty(hostStateTrendRequest.getElementIds())) {
            List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
            return ResultCommon.success(hostStates);
        } else {
            List<HostState> hostStates = hostStateService.getSceneHostStateTrend(hostStateTrendRequest);
            return ResultCommon.success(hostStates);
        }
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
        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);

        return ResultCommon.success(hostStates);
    }


    /**
     * 查询节点当前状态信息
     *
     * @return
     */
    @ApiOperation(value = "节点主机状态")
    @GetMapping()
    public ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest) {
        pageRequest.setPage(1);
        pageRequest.setPageSize(1);
        List<HostState> hostStates = hostStateService.listHostStates(pageRequest);
        return ResultCommon.success(hostStates);
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
    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pagePorts(listPageInfoQueryParams).asResult();
    }

    @ApiOperation(value = "节点进程信息")
    @GetMapping("/element_process")
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pageProcesses(listPageInfoQueryParams).asResult();
    }

    @ApiOperation(value = "节点网卡信息")
    @GetMapping("/element_netifaces")
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        return hostStateService.pageNetifaces(listPageInfoQueryParams).asResult();
    }

    @ApiOperation(value = "主机信息json导出")
    @GetMapping("/export/hosts")
    public void exportHosts(HttpServletResponse response, HostStateTrendRequest hostStateTrendRequest, TimeListPageInfoQueryParams listPageInfoQueryParams) throws JsonProcessingException {
        ListObjServiceResult<Netiface> netiface = hostStateService.pageNetifaces(listPageInfoQueryParams).asResult();
        ListObjServiceResult<Port> port = hostStateService.pagePorts(listPageInfoQueryParams).asResult();
        ListObjServiceResult<Process> process = hostStateService.pageProcesses(listPageInfoQueryParams).asResult();
        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
        HostExportState hostExportState = new HostExportState();
        hostExportState.setHostState(hostStates);
        hostExportState.setNetiface(netiface);
        hostExportState.setPorts(port);
        hostExportState.setProcess(process);
        ObjectMapper objectMapper = new ObjectMapper();
        String fileName = "tmp/host.json";
        exportData(response, objectMapper, fileName, objectMapper.writeValueAsString(hostExportState));
    }

    private void exportData(HttpServletResponse response, ObjectMapper objectMapper, String fileName, String s) {
        File file = new File(fileName);
        if (file.exists()) {
            FileUtil.deleteDir(file);
        }
        try {
            String jsonString = s;
            objectMapper.writeValue(file, jsonString);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtil.setResponse(file, true, response);
    }


    @ApiOperation(value = "主机采集信息json导出")
    @GetMapping("/export/hosts/acquisition")
    public void exportHostAcquisition(HttpServletResponse response, ElementMonitorRequest pageRequest, @RequestParam(required = false) String path,
                                      @RequestParam(required = false) Integer intervalMinutes,
                                      @RequestParam(required = false) Integer bucketSize) throws JsonProcessingException {
        // 网络接口列表
        ListObjServiceResult<CollectionHostEventBO> modifyFileCount = sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP, path);
        ListObjResult<CollectionHostEventBO> data = modifyFileCount.getData();
        List<CollectionHostEventBO> list = data.getList();
        if (Objects.isNull(intervalMinutes)) {
            intervalMinutes = 15;
        }
        if (Objects.isNull(bucketSize)) {
            bucketSize = 15;
        }
        List<ElementHistogramInfo> modifyFileList = sceneElementMonitorService.listEventHistogram(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP,
                intervalMinutes, bucketSize);
        FileExportState fileExportState = new FileExportState();
        fileExportState.setModifyFileCount(list);
        fileExportState.setModifyFileList(modifyFileList);
        ObjectMapper objectMapper = new ObjectMapper();
        String fileName = "tmp/host_acqu.json";
        exportData(response, objectMapper, fileName, objectMapper.writeValueAsString(fileExportState));
    }



    @ApiOperation(value = "流量采集数据json导出")
    @GetMapping("/export/flow/acquisition")
    public void exportFlowAcquisition(HttpServletResponse response, HostStateTrendRequest hostStateTrendRequest) throws JsonProcessingException {
        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        String fileName = "tmp/flow.json";
        exportData(response, objectMapper, fileName, objectMapper.writeValueAsString(hostStates));
    }

    @ApiOperation(value = "漏洞数据json导出")
    @GetMapping("/export/vuln")
    public void exportVulnerabilityInfo(HttpServletResponse response, ElementMonitorRequest pageRequest) throws JsonProcessingException {
        List<ElementVulnerabilityCountInfo> vulnerabilityCountInfo = sceneElementMonitorService.getVulnerabilityCountInfo(pageRequest);
        List<ElementVulnerabilityPieChartInfo> vulnerabilityPieChartInfo = sceneElementMonitorService.getVulnerabilityPieChartInfo(pageRequest);
        ListObjResult<ElementVulnerabilityInfo> vulnerabilityInfo = sceneElementMonitorService.getVulnerabilityInfo(pageRequest);
        List<ElementVulnerabilityInfo> list = vulnerabilityInfo.getList();
        VulnExportState vulnExportState = new VulnExportState();
        vulnExportState.setList(list);
        vulnExportState.setVulnerabilityCountInfo(vulnerabilityCountInfo);
        vulnExportState.setVulnerabilityPieChartInfo(vulnerabilityPieChartInfo);
        ObjectMapper objectMapper = new ObjectMapper();
        String fileName = "tmp/vlun.json";
        exportData(response, objectMapper, fileName, objectMapper.writeValueAsString(vulnExportState));
    }
}