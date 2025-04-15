package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.controller.BaseModelListControllerBase;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.base.util.json.JsonUtils;
import cn.cyberpeace.mp.cscene.CscenePluginDataRecordService;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.controller.response.*;
import cn.cyberpeace.mp.cscene.dto.element.*;
import cn.cyberpeace.mp.cscene.dto.flow.FlowPcapFileInfo;
import cn.cyberpeace.mp.cscene.dto.logfile.LogFileVO;
import cn.cyberpeace.mp.cscene.request.agent.AgentNameBasedRequest;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.request.logfile.LogFileSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.service.*;
import cn.cyberpeace.mp.cscene.util.FileUtil;
import cn.cyberpeace.mp.scene.core.event.base.EventStatus;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionPluginSceneDataModel;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.util.FlowPcapExportUtil;
import cn.cyberpeace.mp.situationaware.model.enums.FileEventType;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:22
 */
@Api(value = "主机状态监控", tags = "主机状态监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/hoststate")
public class HostStateController implements BaseModelListControllerBase {

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private FlowMonitorService flowMonitorService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private CsceneInstanceService modelService;

    @Autowired
    private LogFileService logFileService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private CscenePluginDataRecordService dataRecordService;

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
     *
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
        ListObjServiceResult<CollectionHostEventBO> hostStates = new ListObjServiceResult<>();
        List<JSONObject> customDataPage = new ArrayList<>();
        if (Objects.nonNull(queryParams.getType()) && queryParams.getType() != 0) {
            // 传入的sceneId不需要提前转
            //文件变更
            if (queryParams.getType().equals(0)) {
                hostStates = modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.FILE_MODIFY,
                        queryParams, FileEventType.FILE.name());
            }
            //注册变更
            if (queryParams.getType().equals(1)) {
                hostStates = modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.REGISTRY_MODIFY, queryParams,
                        FileEventType.REGISTRY.name());
            }
            //原始日志
            if (queryParams.getType().equals(2)) {
                // yz4的原始日志不走es的方式
                customDataPage = logFileService.getCustomDataPage(id, 1, 100);
            }
            //操作系统日志
            if (queryParams.getType().equals(3)) {

                hostStates = modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.OS_LOG, queryParams,
                        FileEventType.OS_LOG.name());
            }
            //应用系统日志
            if (queryParams.getType().equals(4)) {

                hostStates = modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.APPLICATION_LOG, queryParams,
                        FileEventType.APPLICATION_LOG.name());
            }
            //安全系统日志
            if (queryParams.getType().equals(5)) {

                hostStates = modelService.getProbeCollectionData(null, id, ProbeConstant.ProbeId.SECURITY_LOG, queryParams,
                        FileEventType.SECURITY_LOG.name());
            }
            //自定义采集
            if (queryParams.getType().equals(6)) {
                hostStates = modelService.listHostEvents(id, queryParams);
            }
        }


        ObjectMapper objectMapper = new ObjectMapper();
        String fileName = "/tmp/host_acqu.json";
        File file = new File(fileName);
        if (file.exists()) {
            FileUtil.deleteDir(file);
        }
        try {
            String jsonString = "";
            if(queryParams.getType().equals(2)){
                jsonString = JsonUtils.stringify(customDataPage);
            }else{
                List<CollectionHostEventBO> result = hostStates.getSource().getObjs();
                jsonString = JsonUtils.stringify(result);
            }
            objectMapper.writeValue(file, jsonString);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20"));
        } catch (IOException e) {
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

    /**
     * 主机信息Excel导出
     * @param response
     * @param hostInfoExportParam
     */
    @ApiOperation(value = "主机信息Excel导出")
    @PostMapping("/export_excel/hosts")
    public void exportExcelHosts(HttpServletResponse response, @RequestBody List<HostInfoExportQueryParam> hostInfoExportParam) {

        log.info("主机信息Excel导出 param:{}", hostInfoExportParam);
        String destXlsPath = "/cpms/excel/export_host_info.xls";
        // 本地测试
//        String destXlsPath = "C:/data/yz1/excel/export_host_info.xls";

        File file = new File(destXlsPath);
        if (!file.getParentFile().exists()) {
            boolean mkdirs = file.getParentFile().mkdirs();
        }

        ExcelWriter writer = null;
        try {
            writer = EasyExcel.write(file).build();
            for (int i = 0; i < hostInfoExportParam.size(); i++) {

                // 机器基本信息
                List<HostBasicInfoExportResponse> basicInfo = getBasicInfo(hostInfoExportParam.get(i).getHostInfoQueryParams());
                // CPU占用率 内存占用率 磁盘占用率
                List<HostTrendInfoExportResponse> trendInfo = getTrendInfo(hostInfoExportParam.get(i).getHostInfoQueryParams());
                // 网络接口列表
                List<HostNetifacesInfoResponse> netifacesInfo = getNetifaces(hostInfoExportParam.get(i).getNetifacesInfoQueryParams());
                // 端口列表
                List<HostPortInfoExportResponse> portInfo = getPorts(hostInfoExportParam.get(i).getPortsInfoQueryParams());
                // 进程列表
                List<HostProcessInfoExportResponse> processInfo = getProcesses(hostInfoExportParam.get(i).getProcessesInfoQueryParams());

                WriteSheet writeSheet = EasyExcel.writerSheet(i ,hostInfoExportParam.get(i).getHostName()).needHead(Boolean.FALSE).build();
                // 机器基本信息
                WriteTable writeTable1 = EasyExcel.writerTable(0).head(HostBasicInfoExportResponse.class).needHead(Boolean.TRUE).build();
                // CPU占用率 内存占用率 磁盘占用率
                WriteTable writeTable2 = EasyExcel.writerTable(1).head(HostTrendInfoExportResponse.class).needHead(Boolean.TRUE).build();
                // 网络接口列表
                WriteTable writeTable3 = EasyExcel.writerTable(2).head(HostNetifacesInfoResponse.class).needHead(Boolean.TRUE).build();
                // 端口列表
                WriteTable writeTable4 = EasyExcel.writerTable(3).head(HostPortInfoExportResponse.class).needHead(Boolean.TRUE).build();
                // 进程列表
                WriteTable writeTable5 = EasyExcel.writerTable(4).head(HostProcessInfoExportResponse.class).needHead(Boolean.TRUE).build();

                // 写入机器基本信息
                writer.write(basicInfo, writeSheet, writeTable1);
                // 写入CPU占用率 内存占用率 磁盘占用率
                writer.write(trendInfo, writeSheet, writeTable2);
                // 写入网络接口列表
                writer.write(netifacesInfo, writeSheet, writeTable3);
                // 写入端口列表
                writer.write(portInfo, writeSheet, writeTable4);
                // 写入进程列表
                writer.write(processInfo, writeSheet, writeTable5);
            }
        } catch (Exception e) {
            log.error("导出主机信息Excel失败", e);
            throw new MpRuntimeException("导出主机信息Excel失败");
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }

        FileUtil.setResponse(file, true, response);
    }

    /**
     * 主机采集数据信息Excel导出
     * @param response
     * @param queryParams
     */
    @ApiOperation(value = "主机采集数据信息Excel导出")
    @PostMapping("/export_excel/hosts/acquisition")
    public void exportExcelHostAcquisition(HttpServletResponse response, @RequestBody HostsAcquisitionQueryParams queryParams) {
        log.info("入参 : {}", queryParams);
        String destXlsPath = "/cpms/excel/export_host_acquisition_info.xls";
        // 本地测试
//        String destXlsPath = "C:/data/yz1/excel/export_host_acquisition_info.xls";

        File file = new File(destXlsPath);
        if (!file.getParentFile().exists()) {
            boolean mkdirs = file.getParentFile().mkdirs();
        }
        // 查询文件变更数量
        List<HostsAcquisitionFileNumExportResponse> fileNumExportResponses = getListEventHistogram(queryParams.getElementMonitorRequest(), "syscheck_file");
        // 查询文件变更列表
        List<HostAcquisitionFileExportResponse> fileExportResponses = getModifyData(queryParams.getElementMonitorRequest());
        // 查询注册表变更数量
        List<HostsAcquisitionFileNumExportResponse> registryNumExportResponses = getListEventHistogram(queryParams.getElementMonitorRequest(), "syscheck_registry");
        // 查询注册表变更列表
        List<HostAcquisitionRegistryExportResponse> registryModify = getRegistryModify(queryParams.getElementMonitorRequest());
        // 查询原始日志
        List<HostAcquisitionLogEntExportResponse> pageLogEntFiles = getPageLogEntFiles(queryParams.getLogFileSceneElementPageRequest());
        // 查询 Windows事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020101"));
        List<HostAcquisitionEventExportResponse> listWindowsHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 Linux事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020102"));
        List<HostAcquisitionEventExportResponse> listLinuxHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 Cisco事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020103"));
        List<HostAcquisitionEventExportResponse> listCiscoHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 HP事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020104"));
        List<HostAcquisitionEventExportResponse> listHPHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 BSD事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020105"));
        List<HostAcquisitionEventExportResponse> listBSDHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 Junos事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020106"));
        List<HostAcquisitionEventExportResponse> listJunosHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 Solaris事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020107"));
        List<HostAcquisitionEventExportResponse> listSolarisHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 syslog
        List<HostAcquisitionSyslogExportResponse> listSyslogHostEvents = getListSyslogHostEvents(queryParams.getHostEventQueryParams());
        // 查询 邮件服务器事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020201"));
        List<HostAcquisitionEventExportResponse> listMailHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 WEB服务器事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020202"));
        List<HostAcquisitionEventExportResponse> listWebHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 数据库事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020203"));
        List<HostAcquisitionEventExportResponse> listDatabaseHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 网络服务事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020204"));
        List<HostAcquisitionEventExportResponse> listNetworkServiceHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 应用软件事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020205"));
        List<HostAcquisitionEventExportResponse> listApplicationSoftwareHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 云事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020206"));
        List<HostAcquisitionEventExportResponse> listCloudHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 负载均衡事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020207"));
        List<HostAcquisitionEventExportResponse> listLoadBalancingHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 应用框架事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020208"));
        List<HostAcquisitionEventExportResponse> listApplicationFrameworkHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 文件服务器事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020210"));
        List<HostAcquisitionEventExportResponse> listFileServerHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 AV事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020301"));
        List<HostAcquisitionEventExportResponse> listAVHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 FW事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020302"));
        List<HostAcquisitionEventExportResponse> listFWHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 IDS/IPS事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020303"));
        List<HostAcquisitionEventExportResponse> listIDSIPSHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 VPN事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020304"));
        List<HostAcquisitionEventExportResponse> listVPNHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 WAF事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020313"));
        List<HostAcquisitionEventExportResponse> listWAFHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询 其他事件日志
        queryParams.getHostEventQueryParams().setCatagories(List.of("020305"));
        List<HostAcquisitionEventExportResponse> listOtherHostEvents = getListHostEvents(queryParams.getHostEventQueryParams());
        // 查询自定义采集
        List<HostAcquisitionProbeExportResponse> probeCollectionData = getProbeCollectionData(queryParams.getHostEventQueryParams());

        ExcelWriter writer = null;
        try {
            writer = EasyExcel.write(file).build();
            // 需要导出的sheet页
            WriteSheet writeSheet1 = EasyExcel.writerSheet(0,"文件变更事件").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet2 = EasyExcel.writerSheet(1,"注册表变更事件").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet3 = EasyExcel.writerSheet(2,"原始日志").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet4 = EasyExcel.writerSheet(3,"Windows").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet5 = EasyExcel.writerSheet(4,"Linux").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet6 = EasyExcel.writerSheet(5,"Cisco").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet7 = EasyExcel.writerSheet(6,"HP").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet8 = EasyExcel.writerSheet(7,"BSD").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet9 = EasyExcel.writerSheet(8,"Junos").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet10 = EasyExcel.writerSheet(9,"solaris").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet11 = EasyExcel.writerSheet(10,"syslog").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet12 = EasyExcel.writerSheet(11,"邮件服务器").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet13 = EasyExcel.writerSheet(12,"WEB服务器").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet14 = EasyExcel.writerSheet(13,"数据库").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet15 = EasyExcel.writerSheet(14,"网络服务").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet16 = EasyExcel.writerSheet(15,"应用软件").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet17 = EasyExcel.writerSheet(16,"云").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet18 = EasyExcel.writerSheet(17,"负载均衡").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet19 = EasyExcel.writerSheet(18,"应用框架").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet20 = EasyExcel.writerSheet(19,"文件服务器").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet21 = EasyExcel.writerSheet(20,"AV").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet22 = EasyExcel.writerSheet(21,"FW").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet23 = EasyExcel.writerSheet(22,"IDS_IPS").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet24 = EasyExcel.writerSheet(23,"VPN").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet25 = EasyExcel.writerSheet(24,"WAF").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet26 = EasyExcel.writerSheet(25,"其他").needHead(Boolean.FALSE).build();
            WriteSheet writeSheet27 = EasyExcel.writerSheet(26,"自定义采集").needHead(Boolean.FALSE).build();

            // 文件或注册表变更数量表头
            WriteTable writeTable1 = EasyExcel.writerTable(0).head(HostsAcquisitionFileNumExportResponse.class).needHead(Boolean.TRUE).build();
            // 文件变更列表表头
            WriteTable writeTable2 = EasyExcel.writerTable(1).head(HostAcquisitionFileExportResponse.class).needHead(Boolean.TRUE).build();
            // 注册表变更列表表头
            WriteTable writeTable3 = EasyExcel.writerTable(1).head(HostAcquisitionRegistryExportResponse.class).needHead(Boolean.TRUE).build();
            // 原始日志列表表头
            WriteTable writeTable4 = EasyExcel.writerTable(0).head(HostAcquisitionLogEntExportResponse.class).needHead(Boolean.TRUE).build();
            // syslog日志列表表头
            WriteTable writeTable5 = EasyExcel.writerTable(0).head(HostAcquisitionSyslogExportResponse.class).needHead(Boolean.TRUE).build();
            // 各类日志列表表头
            WriteTable writeTable6 = EasyExcel.writerTable(0).head(HostAcquisitionEventExportResponse.class).needHead(Boolean.TRUE).build();
            // 自定义采集列表表头
            WriteTable writeTable7 = EasyExcel.writerTable(0).head(HostAcquisitionProbeExportResponse.class).needHead(Boolean.TRUE).build();

            // 写入文件变更数量  写入文件变更列表
            writer.write(fileNumExportResponses, writeSheet1, writeTable1);
            writer.write(fileExportResponses, writeSheet1, writeTable2);
            // 写入注册表变更数量   写入注册表变更列表
            writer.write(registryNumExportResponses, writeSheet2, writeTable1);
            writer.write(registryModify, writeSheet2, writeTable3);
            // 写入原始日志列表
            writer.write(pageLogEntFiles, writeSheet3, writeTable4);
            // 写入Windows日志列表
            writer.write(listWindowsHostEvents, writeSheet4, writeTable6);
            // 写入Linux日志列表
            writer.write(listLinuxHostEvents, writeSheet5, writeTable6);
            // 写入Cisco日志列表
            writer.write(listCiscoHostEvents, writeSheet6, writeTable6);
            // 写入HP日志列表
            writer.write(listHPHostEvents, writeSheet7, writeTable6);
            // 写入BSD日志列表
            writer.write(listBSDHostEvents, writeSheet8, writeTable6);
            // 写入Junos日志列表
            writer.write(listJunosHostEvents, writeSheet9, writeTable6);
            // 写入Solaris日志列表
            writer.write(listSolarisHostEvents, writeSheet10, writeTable6);
            // 写入syslog日志列表
            writer.write(listSyslogHostEvents, writeSheet11, writeTable5);
            // 写入邮件服务器日志列表
            writer.write(listMailHostEvents, writeSheet12, writeTable6);
            // 写入WEB服务器日志列表
            writer.write(listWebHostEvents, writeSheet13, writeTable6);
            // 写入数据库日志列表
            writer.write(listDatabaseHostEvents, writeSheet14, writeTable6);
            // 写入网络服务日志列表
            writer.write(listNetworkServiceHostEvents, writeSheet15, writeTable6);
            // 写入应用软件日志列表
            writer.write(listApplicationSoftwareHostEvents, writeSheet16, writeTable6);
            // 写入云日志列表
            writer.write(listCloudHostEvents, writeSheet17, writeTable6);
            // 写入负载均衡日志列表
            writer.write(listLoadBalancingHostEvents, writeSheet18, writeTable6);
            // 写入应用框架日志列表
            writer.write(listApplicationFrameworkHostEvents, writeSheet19, writeTable6);
            // 写入文件服务器日志列表
            writer.write(listFileServerHostEvents, writeSheet20, writeTable6);
            // 写入AV日志列表
            writer.write(listAVHostEvents, writeSheet21, writeTable6);
            // 写入FW日志列表
            writer.write(listFWHostEvents, writeSheet22, writeTable6);
            // 写入IDS/IPS日志列表
            writer.write(listIDSIPSHostEvents, writeSheet23, writeTable6);
            // 写入VPN日志列表
            writer.write(listVPNHostEvents, writeSheet24, writeTable6);
            // 写入WAF日志列表
            writer.write(listWAFHostEvents, writeSheet25, writeTable6);
            // 写入其他日志列表
            writer.write(listOtherHostEvents, writeSheet26, writeTable6);
            // 写入自定义采集列表
            writer.write(probeCollectionData, writeSheet27, writeTable7);

        } catch (Exception e) {
            log.error("导出主机采集信息Excel失败", e);
            throw new MpRuntimeException("导出主机采集信息Excel失败");
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }

        FileUtil.setResponse(file, true, response);

    }

    /**
     * 获取机器基本信息
     */
    private List<HostBasicInfoExportResponse> getBasicInfo(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        List<ElementMonitorBasicInfo> result = new ArrayList<>();
        String nodeId = listPageInfoQueryParams.getNodeIds().get(0);
        List<ElementMonitorBasicInfo> basicInfoList = sceneMonitorService.getAllSceneElements(listPageInfoQueryParams.getSceneIds().get(0), null, new SceneAllElementsQueryRequest());
        if (CollectionUtils.isNotEmpty(basicInfoList)) {
            for (var e : basicInfoList) {
                if (e.getId().equals(nodeId)) {
                    if (e.getEvent() != null) {
                        if (e.getEvent() != SceneElementModel.Event.DELETE) {
                            result.add(e);
                        } else if (e.getEvent() == SceneElementModel.Event.DELETE && e.getEventStatus() == EventStatus.OVER) {
                            result.add(e);
                        }
                    } else {
                        result.add(e);
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            return new ArrayList<>();
        }

        BaseSceneElementRequest baseRequest = new BaseSceneElementRequest();
        baseRequest.setSceneId(listPageInfoQueryParams.getSceneIds().get(0));
        baseRequest.setElementIds(listPageInfoQueryParams.getNodeIds());
        OS os = sceneElementMonitorService.getOSInfo(baseRequest);

        List<HostBasicInfoExportResponse> response = new ArrayList<>();

        result.forEach(e -> {
            HostBasicInfoExportResponse hostInfoExportResponse = new HostBasicInfoExportResponse();
            hostInfoExportResponse.setName(e.getName());
            hostInfoExportResponse.setIp(e.getPorts().get(0).getIp());
            hostInfoExportResponse.setSystemTypeName(e.getSystemTypeName());
            hostInfoExportResponse.setArchitecture(os == null ? "" : os.getArchitecture());
            hostInfoExportResponse.setVersion(os == null ? "" : os.getVersion());
            response.add(hostInfoExportResponse);
        });

        return response;
    }

    /**
     * 获取主机趋势信息 CPU占用率 内存占用率 磁盘占用率
     * @param listPageInfoQueryParams 文件列表
     * @return
     */
    private List<HostTrendInfoExportResponse> getTrendInfo(TimeListPageInfoQueryParams listPageInfoQueryParams) {

        HostStateTrendRequest hostStateTrendRequest = new HostStateTrendRequest();
        hostStateTrendRequest.setSceneId(listPageInfoQueryParams.getSceneIds().get(0));
        hostStateTrendRequest.setElementIds(listPageInfoQueryParams.getNodeIds());
        List<HostState> hostStates = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
        if (CollectionUtils.isEmpty(hostStates)) {
            return new ArrayList<>();
        }
        List<HostTrendInfoExportResponse> responses = new ArrayList<>();

        hostStates.forEach(e -> {
            HostTrendInfoExportResponse hostTrendInfoExportResponse = new HostTrendInfoExportResponse();
            BeanUtils.copyProperties(e, hostTrendInfoExportResponse);
            responses.add(hostTrendInfoExportResponse);
        });

        return responses;
    }

    /**
     * 获取网络接口列表
     * @param listPageInfoQueryParams
     * @return
     */
    private List<HostNetifacesInfoResponse> getNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        // 节点网卡信息
        List<Netiface> netifaces = hostStateService.pageNetifaces(listPageInfoQueryParams).asResult().getData().getList();

        if (CollectionUtils.isEmpty(netifaces)) {
            return new ArrayList<>();
        }

        List<HostNetifacesInfoResponse> response = new ArrayList<>();
        netifaces.forEach(e -> {
            HostNetifacesInfoResponse hostNetifacesInfoResponse = new HostNetifacesInfoResponse();
            BeanUtils.copyProperties(e, hostNetifacesInfoResponse);
            hostNetifacesInfoResponse.setTxPkts(e.getTx().getPackets());
            hostNetifacesInfoResponse.setRxPkts(e.getRx().getPackets());
            hostNetifacesInfoResponse.setTxBytes(e.getTx().getBytes());
            hostNetifacesInfoResponse.setRxBytes(e.getRx().getBytes());
            response.add(hostNetifacesInfoResponse);
        });

        return response;
    }

    /**
     * 获取端口列表
     * @param listPageInfoQueryParams
     * @return
     */
    private List<HostPortInfoExportResponse> getPorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {

        // 节点端口信息
        List<Port> ports = hostStateService.pagePorts(listPageInfoQueryParams).asResult().getData().getList();
        if (CollectionUtils.isEmpty(ports)) {
            return new ArrayList<>();
        }

        List<HostPortInfoExportResponse> response = new ArrayList<>();
        ports.forEach(e -> {
            HostPortInfoExportResponse hostPortInfoExportResponse = new HostPortInfoExportResponse();
            BeanUtils.copyProperties(e, hostPortInfoExportResponse);
            hostPortInfoExportResponse.setLocalIp(e.getLocal().getIp());
            hostPortInfoExportResponse.setLocalPort(e.getLocal().getPort());
            hostPortInfoExportResponse.setRemoteIp(e.getRemote().getIp());
            hostPortInfoExportResponse.setRemotePort(e.getRemote().getPort());
            response.add(hostPortInfoExportResponse);
        });

        return response;
    }

    /**
     * 获取进程列表
     * @param listPageInfoQueryParams
     * @return
     */
    private List<HostProcessInfoExportResponse> getProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {

        // 节点进程信息
        List<Process> processes = hostStateService.pageProcesses(listPageInfoQueryParams).asResult().getData().getList();

        if (CollectionUtils.isEmpty(processes)) {
            return new ArrayList<>();
        }

        List<HostProcessInfoExportResponse> response = new ArrayList<>();
        processes.forEach(e -> {
            HostProcessInfoExportResponse hostProcessInfoExportResponse = new HostProcessInfoExportResponse();
            BeanUtils.copyProperties(e, hostProcessInfoExportResponse);
            hostProcessInfoExportResponse.setMemorySize(String.format("%.2f", e.getVmSize() / 1024.00));
            response.add(hostProcessInfoExportResponse);
        });
        return response;
    }

    /**
     * 获取文件或注册表变更数量
     * @param pageRequest
     * @param group
     * @return
     */
    private List<HostsAcquisitionFileNumExportResponse> getListEventHistogram(ElementMonitorRequest pageRequest, String group) {
        List<HostsAcquisitionFileNumExportResponse> response = new ArrayList<>();
        if (CsceneBizConstant.FILE_MODIFY_GROUP.contains(group)) {
            List<ElementHistogramInfo> elementHistogramInfos = sceneElementMonitorService
                    .listEventHistogram(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP, 15, 15);
            if (CollectionUtils.isEmpty(elementHistogramInfos)) {
                return new ArrayList<>();
            }

            elementHistogramInfos.forEach(e -> {
                HostsAcquisitionFileNumExportResponse hostsAcquisitionExportResponse = new HostsAcquisitionFileNumExportResponse();
                BeanUtils.copyProperties(e, hostsAcquisitionExportResponse);
                response.add(hostsAcquisitionExportResponse);
            });
        }

        if (CsceneBizConstant.REGISTRY_MODIFY_GROUP.contains(group)) {
            List<ElementHistogramInfo> elementHistogramInfos = sceneElementMonitorService
                    .listEventHistogram(pageRequest, CsceneBizConstant.REGISTRY_MODIFY_GROUP, 15, 15);
            if (CollectionUtils.isEmpty(elementHistogramInfos)) {
                return new ArrayList<>();
            }

            elementHistogramInfos.forEach(e -> {
                HostsAcquisitionFileNumExportResponse hostsAcquisitionExportResponse = new HostsAcquisitionFileNumExportResponse();
                BeanUtils.copyProperties(e, hostsAcquisitionExportResponse);
                response.add(hostsAcquisitionExportResponse);
            });
        }

        return response;
    }

    /**
     * 获取文件变更列表
     * @param pageRequest
     * @return
     */
    private List<HostAcquisitionFileExportResponse> getModifyData(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();
        ListObjResult<CollectionHostEventBO> data = sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP, pageRequest.getPath()).getData();

        if (data == null || CollectionUtils.isEmpty(data.getList())) {
            return new ArrayList<>();
        }
        List<CollectionHostEventBO> list = data.getList();
        List<HostAcquisitionFileExportResponse> response = new ArrayList<>();
        list.forEach(e -> {
            HostAcquisitionFileExportResponse hostAcquisitionExportResponse = new HostAcquisitionFileExportResponse();
            BeanUtils.copyProperties(e, hostAcquisitionExportResponse);
            response.add(hostAcquisitionExportResponse);
        });

        return response;
    }

    /**
     * 获取注册表变成列表
     * @param pageRequest
     * @return
     */
    private List<HostAcquisitionRegistryExportResponse> getRegistryModify(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();
        ListObjResult<CollectionHostEventBO> data = sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP, pageRequest.getPath()).getData();

        if (data == null || CollectionUtils.isEmpty(data.getList())) {
            return new ArrayList<>();
        }
        List<CollectionHostEventBO> list = data.getList();
        List<HostAcquisitionRegistryExportResponse> response = new ArrayList<>();
        list.forEach(e -> {
            HostAcquisitionRegistryExportResponse hostAcquisitionRegistryExportResponse = new HostAcquisitionRegistryExportResponse();
            BeanUtils.copyProperties(e, hostAcquisitionRegistryExportResponse);
            response.add(hostAcquisitionRegistryExportResponse);
        });

        return response;
    }

    /**
     * 获取原始日志列表
     * @param pageRequest
     */
    private List<HostAcquisitionLogEntExportResponse> getPageLogEntFiles(LogFileSceneElementPageRequest pageRequest) {
        ListObjResult<LogFileVO> data = getPageLogFiles(pageRequest).getData();
        if (data == null || CollectionUtils.isEmpty(data.getList())) {
            return new ArrayList<>();
        }
        List<LogFileVO> list = data.getList();
        List<HostAcquisitionLogEntExportResponse> responses = new ArrayList<>();
        list.forEach(e -> {
            HostAcquisitionLogEntExportResponse hostAcquisitionLogEntExportResponse = new HostAcquisitionLogEntExportResponse();
            BeanUtils.copyProperties(e, hostAcquisitionLogEntExportResponse);
            responses.add(hostAcquisitionLogEntExportResponse);
        });
        return responses;
    }

    /**
     * 获取syslog列表
     * @param queryParams
     * @return
     */
    private List<HostAcquisitionSyslogExportResponse> getListSyslogHostEvents(HostEventQueryParams queryParams) {
        queryParams.setCatagories(List.of("020109"));
        queryParams.setIsSyslog(true);
        handleQueryParams("listHostEvents", queryParams);
        ListObjResult<CollectionHostEventBO> data = modelService.listHostEvents(queryParams.getId(), queryParams).asResult().getData();

        if (data == null || CollectionUtils.isEmpty(data.getList())) {
            return new ArrayList<>();
        }
        List<CollectionHostEventBO> list = data.getList();
        List<HostAcquisitionSyslogExportResponse> response = new ArrayList<>();
        list.forEach(e -> {
            HostAcquisitionSyslogExportResponse hostAcquisitionEventExportResponse = new HostAcquisitionSyslogExportResponse();
            BeanUtils.copyProperties(e, hostAcquisitionEventExportResponse);
            response.add(hostAcquisitionEventExportResponse);
        });
        return response;
    }

    /**
     * 获取自定义采集日志
     * @param queryParams
     * @return
     */
    private List<HostAcquisitionProbeExportResponse> getProbeCollectionData(HostEventQueryParams queryParams) {
        queryParams.setCatagories(null);
        queryParams.setIsSyslog(null);
        handleQueryParams("getProbeCollectionData", queryParams);
        ListObjResult<CollectionHostEventBO> data = modelService.getProbeCollectionData(queryParams.getRequestId(),
                queryParams.getId(), queryParams.getProbeId(), queryParams, null).asResult().getData();

        if (data == null || CollectionUtils.isEmpty(data.getList())) {
            return new ArrayList<>();
        }
        List<CollectionHostEventBO> list = data.getList();
        List<HostAcquisitionProbeExportResponse> response = new ArrayList<>();
        list.forEach(e -> {
            HostAcquisitionProbeExportResponse hostAcquisitionProbeExportResponse = new HostAcquisitionProbeExportResponse();
            BeanUtils.copyProperties(e, hostAcquisitionProbeExportResponse);
            hostAcquisitionProbeExportResponse.setContent(JSONObject.toJSONString(e.getContent()));
            response.add(hostAcquisitionProbeExportResponse);
        });
        return response;
    }

    /**
     * 获取其他的各类日志
     * @param queryParams
     * @return
     */
    private List<HostAcquisitionEventExportResponse> getListHostEvents(HostEventQueryParams queryParams) {
        handleQueryParams("listHostEvents", queryParams);
        ListObjResult<CollectionHostEventBO> data = modelService.listHostEvents(queryParams.getId(), queryParams).asResult().getData();
        if (data == null || CollectionUtils.isEmpty(data.getList())) {
            return new ArrayList<>();
        }
        List<CollectionHostEventBO> list = data.getList();
        List<HostAcquisitionEventExportResponse> response = new ArrayList<>();
        list.forEach(e -> {
            HostAcquisitionEventExportResponse hostAcquisitionEventExportResponse = new HostAcquisitionEventExportResponse();
            Integer level = hostAcquisitionEventExportResponse.getLevel();
            // 0-4 低 5-9 中 10-- 高
            hostAcquisitionEventExportResponse.setLevelName(level == null ? "未知" : level > 9 ? "高" : level < 5 ? "低" : "中");
            BeanUtils.copyProperties(e, hostAcquisitionEventExportResponse);
            response.add(hostAcquisitionEventExportResponse);
        });
        return response;

    }

    /**
     * 原始日志
     * @param pageRequest
     * @return
     */
    private ListObjServiceResult<LogFileVO> getPageLogFiles(LogFileSceneElementPageRequest pageRequest) {
        log.info("page query log files, pageRequest = {}, sceneId = {}", pageRequest, pageRequest.getSceneId());
        if (StringUtils.isNotEmpty(pageRequest.getStartTime()) || StringUtils.isNotEmpty(pageRequest.getEndTime())) {
            var instance = modelService.get(pageRequest.getSceneId()).getSource().getObj();
            LocalDateTime startTime = null;
            if (StringUtils.isNotEmpty(pageRequest.getStartTime())) {
                startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
            } else {
                startTime = instance.getCreateTime();
            }
            LocalDateTime endTime = null;
            if (StringUtils.isNotEmpty(pageRequest.getEndTime())) {
                endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
            } else {
                endTime = LocalDateTime.now();
            }
            TimeListPageInfoQueryParams params = new TimeListPageInfoQueryParams();
            params.setSceneIds(List.of(instance.getScene()));
            params.setNodeIds(pageRequest.getElementIds());
            var dataModels = dataRecordService.getData(params, CollectionPluginSceneDataModel.Type.LOGFile, startTime,
                    endTime);
            var logs = dataModels.stream().map(d -> {
                return JsonCommon.parse(d.getData(), LogFileVO.class);
            }).collect(Collectors.toList());
            Set<String> exists = new HashSet<>();
            List<LogFileVO> finalResult = new ArrayList<>();
            for (var log : logs) {
                if (!exists.contains(log.getPath())) {
                    exists.add(log.getPath());
                    finalResult.add(log);
                }
            }
            return new ListObjServiceResult(new ListObjResult(finalResult, finalResult.size()).asResult()).asResult();
        } else {
            ListObjServiceResult<LogFileVO> result = logFileService.pageByElementId(pageRequest);
            return result;
        }
    }


}