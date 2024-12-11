package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.dto.element.*;
import cn.cyberpeace.mp.cscene.dto.flow.FlowPcapFileInfo;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.FlowMonitorService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.cscene.util.FileUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.util.FlowPcapExportUtil;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
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
    private FlowMonitorService flowMonitorService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private CsceneInstanceService modelService;

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
        String fileName = "/home/mp/host.json";
        exportData(response, objectMapper, fileName, JSON.toJSONString(hostExportState));
    }

    private void exportData(HttpServletResponse response, ObjectMapper objectMapper, String fileName, String s) {
        File file = new File(fileName);
        if (file.exists()) {
            FileUtil.deleteDir(file);
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(s);
            fileWriter.close();
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
        String fileName = "/home/mp/host_acqu.json";
        exportData(response, objectMapper, fileName, objectMapper.writeValueAsString(fileExportState));
    }

    @ApiOperation(value = "主机信息json导出")
    @PostMapping("/export/hosts/acquisition")
    public void exportHostAcquisition(HttpServletResponse response, String id, HostEventQueryParams queryParams) {
        ListObjServiceResult<CollectionHostEventBO> hostStates = new ListObjServiceResult<>() ;
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() != 0) {
            // 传入的sceneId不需要提前转
            //文件变更
            if (queryParams.getType().equals(0)) {
                hostStates=modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.FILE_MODIFY,
                        queryParams, FileEventType.FILE.name());
            }
            //注册变更
            if (queryParams.getType().equals(1)) {
                hostStates= modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.REGISTRY_MODIFY, queryParams,
                        FileEventType.REGISTRY.name());
            }
            //原始日志
            if (queryParams.getType().equals(2)) {

                hostStates= modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.LOG_FILE, queryParams,
                        FileEventType.LOG.name());
            }
            //操作系统日志
            if (queryParams.getType().equals(3)) {

                hostStates= modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.OS_LOG, queryParams,
                        FileEventType.OS_LOG.name());
            }
            //应用系统日志
            if (queryParams.getType().equals(4)) {

                hostStates= modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.APPLICATION_LOG, queryParams,
                        FileEventType.APPLICATION_LOG.name());
            }
            //安全系统日志
            if (queryParams.getType().equals(5)) {

                hostStates= modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.SECURITY_LOG, queryParams,
                        FileEventType.SECURITY_LOG.name());
            }
            //自定义采集
            else  {
                hostStates = modelService.listHostEvents(id, queryParams);
            }

        }


        ObjectMapper objectMapper = new ObjectMapper();
        String fileName = "/tmp/host_acqu.json";
        File file = new File(fileName);
        if (file.exists()) {
            FileUtil.deleteDir(file);
        }
        try{
            String jsonString = objectMapper.writeValueAsString(hostStates);
            objectMapper.writeValue(file, jsonString);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+","%20"));

        } catch (IOException e){
            e.printStackTrace();
        }
        FileUtil.setResponse(file, true, response);
    }



//    @ApiOperation(value = "流量采集数据json导出")
//    @GetMapping("/export/flow/acquisition")
//    public void exportFlowAcquisition(HttpServletResponse response, HostStateTrendRequest hostStateTrendRequest) throws JsonProcessingException {
//        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String fileName = "/home/mp/flow.json";
//        exportData(response, objectMapper, fileName, objectMapper.writeValueAsString(hostStates));
//    }


    @ApiOperation(value = "流量采集数据json导出")
    @GetMapping("/export/flow/acquisition")
    public void exportFlowAcquisition(HttpServletResponse response, HostStateTrendRequest hostStateTrendRequest) throws JsonProcessingException {
        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // 注册 JavaTimeModule

        String fileName = "/home/mp/flow.json";
        exportData(response, objectMapper, fileName, objectMapper.writeValueAsString(hostStates));
    }

    @ApiOperation(value = "流量采集数据pcap导出")
    @GetMapping("/export/flow/acquisition/pcap")
    public ServiceResult<FlowPcapFileInfo> exportFlowAcquisitionPcap(HostStateTrendRequest hostStateTrendRequest) throws JsonProcessingException {
        FlowPcapFileInfo pcapFileInfo = new FlowPcapFileInfo();
        String taskId = hostStateTrendRequest.getSceneId();
        if (FlowPcapExportUtil.initProcess(taskId)) {
            TaskCommon.executeTask(new Runnable() {
                @Override
                public void run() {
                    flowMonitorService.export(taskId, hostStateTrendRequest.getSceneId());
                }
            });
        }
        pcapFileInfo.setTaskId(taskId);
        return new ServiceResult<>(pcapFileInfo).asResult();
    }

    @ApiOperation(value = "流量导出进度")
    @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "任务id", dataTypeClass = String.class)})
    @GetMapping("/export/flow/process")
    public ServiceResult<FlowPcapExportUtil.FlowPcapExportProcess> process(String id) {
        return new ServiceResult<>(FlowPcapExportUtil.getProcess(id)).asResult();
    }

    @ApiOperation(value = "漏洞数据json导出")
    @GetMapping("/export/vuln")
    public void exportVulnerabilityInfo(HttpServletResponse response, ElementMonitorRequest pageRequest) throws JsonProcessingException {
        List<ElementVulnerabilityCountInfo> vulnerabilityCountInfo = sceneElementMonitorService.getVulnerabilityCountInfo(pageRequest);
        List<ElementVulnerabilityPieChartInfo> vulnerabilityPieChartInfo = sceneElementMonitorService.getVulnerabilityPieChartInfo(pageRequest);
        ListObjResult<ElementVulnerabilityInfo> vulnerabilityInfo = sceneElementMonitorService.getVulnerabilityInfo(pageRequest);
        List<ElementVulnerabilityInfo> list = vulnerabilityInfo.getObjs();
        VulnExportState vulnExportState = new VulnExportState();
        vulnExportState.setList(list);
        vulnExportState.setVulnerabilityCountInfo(vulnerabilityCountInfo);
        vulnExportState.setVulnerabilityPieChartInfo(vulnerabilityPieChartInfo);
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 Java 8 时间模块
        objectMapper.registerModule(new JavaTimeModule());
        String fileName = "/home/mp/vlun.json";
        String string = objectMapper.writeValueAsString(vulnExportState);
        exportData(response, objectMapper, fileName, string);
    }
}