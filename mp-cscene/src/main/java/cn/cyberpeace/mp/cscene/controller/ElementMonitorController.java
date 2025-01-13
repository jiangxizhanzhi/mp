package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.mptool.CollectionUtil;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.controller.response.VulnerabilityCountInfoResponse;
import cn.cyberpeace.mp.cscene.controller.response.FlowOrVulnerabilityExportQueryParam;
import cn.cyberpeace.mp.cscene.controller.response.VulnerabilityInfoExportResponse;
import cn.cyberpeace.mp.cscene.controller.response.VulnerabilityPieChartInfoExportResponse;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementHistogramInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityCountInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityPieChartInfo;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.cscene.util.FileUtil;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 10:16
 */
@Api(value = "节点监控", tags = "节点监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/scene/element")
public class ElementMonitorController {

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;


    @Autowired
    private HostStateService hostStateService;

    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询节点基本信息")
    @GetMapping("/info")
    public ServiceResult<ElementMonitorBasicInfo> getElementMonitorInfo(BaseSceneElementRequest baseRequest) {
        ElementMonitorBasicInfo elementMonitorBasicInfo = sceneElementMonitorService.getElementMonitorInfo(baseRequest, true);

        return ResultCommon.success(elementMonitorBasicInfo);
    }


    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询节点实时状态")
    @GetMapping("/rtinfo")
    public ServiceResult<ElementRTInfo> getElementRTInfo(BaseSceneElementRequest baseRequest) {
        ElementRTInfo rtInfo = sceneElementMonitorService.getElementRTInfo(baseRequest);
        return ResultCommon.success(rtInfo);
    }


    @ApiOperation(value = "节点操作系统信息")
    @GetMapping("/os")
    public ServiceResult<OS> getOS(BaseSceneElementRequest baseRequest) {
        OS os = sceneElementMonitorService.getOSInfo(baseRequest);
        return ResultCommon.success(os);
    }


    @ApiOperation(value = "查询节点网络连接状态")
    @GetMapping("/connection_state")
    public ListObjServiceResult<ConnectionState> pageConnectionStates(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();

        return sceneElementMonitorService.pageNetworkConnections(pageRequest);
    }

    @ApiOperation(value = "查询节点文件变更事件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "文件路径 模糊查询", dataTypeClass = String.class)
    })
    @GetMapping("/file_modify")
    public ListObjServiceResult<CollectionHostEventBO> fileModify(ElementMonitorRequest pageRequest, @RequestParam(required = false) String path) {
        pageRequest.ensure();

        return sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP, path).asResult();
    }

    @ApiOperation(value = "节点文件变更事件柱状图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "intervalMinutes", value = "时间间隔,单位分钟,默认15", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "bucketSize", value = "分桶数量,默认15", dataTypeClass = Integer.class)
    })
    @GetMapping("/file_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> fileModifyHistogram(@RequestParam(required = false) Integer intervalMinutes,
                                                                         @RequestParam(required = false) Integer bucketSize,
                                                                         ElementMonitorRequest pageRequest) {
        if (Objects.isNull(intervalMinutes)) {
            intervalMinutes = 15;
        }
        if (Objects.isNull(bucketSize)) {
            bucketSize = 15;
        }
        return new ServiceResult<>(sceneElementMonitorService.listEventHistogram(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP,
                intervalMinutes, bucketSize)).asResult();
    }

    @ApiOperation(value = "查询节点注册表变更事件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "注册表路径 模糊查询", dataTypeClass = String.class)
    })
    @GetMapping("/registry_modify")
    public ListObjServiceResult<CollectionHostEventBO> registryModify(ElementMonitorRequest pageRequest, @RequestParam(required = false) String path) {
        pageRequest.ensure();

        return sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.REGISTRY_MODIFY_GROUP, path).asResult();
    }

    @ApiOperation(value = "节点注册表变更事件柱状图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "intervalMinutes", value = "时间间隔,单位分钟,默认15", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "bucketSize", value = "分桶数量,默认15", dataTypeClass = Integer.class)
    })
    @GetMapping("/registry_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> registryModifyHistogram(@RequestParam(required = false) Integer intervalMinutes,
                                                                             @RequestParam(required = false) Integer bucketSize,
                                                                             ElementMonitorRequest pageRequest) {
        if (Objects.isNull(intervalMinutes)) {
            intervalMinutes = 15;
        }
        if (Objects.isNull(bucketSize)) {
            bucketSize = 15;
        }
        return new ServiceResult<>(sceneElementMonitorService.listEventHistogram(pageRequest, CsceneBizConstant.REGISTRY_MODIFY_GROUP,
                intervalMinutes, bucketSize)).asResult();
    }

    @ApiOperation(value = "查询节点服务漏洞Top10")
    @GetMapping("/get_service_vulnerability")
    public ListObjServiceResult<ElementVulnerabilityCountInfo> getServiceVulnerability(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();

        var result = sceneElementMonitorService.getVulnerabilityCountInfo(pageRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }

    @ApiOperation(value = "查询节点服务漏洞饼状图")
    @GetMapping("/get_vulnerability_pie_chart")
    public ListObjServiceResult<ElementVulnerabilityPieChartInfo> getVulnerabilityPieChart(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();

        var result = sceneElementMonitorService.getVulnerabilityPieChartInfo(pageRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }

    @ApiOperation(value = "查询节点服务漏洞列表")
    @GetMapping("/get_vulnerability_list")
    public ListObjServiceResult<ElementVulnerabilityInfo> getVulnerabilityList(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();

        var result = sceneElementMonitorService.getVulnerabilityInfo(pageRequest);
        return new ListObjServiceResult<>(result).asResult();
    }

    @ApiOperation(value = "漏洞信息Excel导出")
    @PostMapping("/export_excel/vulnerability")
    public void exportExcelVulnerability(HttpServletResponse response, @RequestBody List<FlowOrVulnerabilityExportQueryParam> queryParams) {
        log.info("漏洞信息导出入参 : {}", queryParams);

//        String destXlsPath = "/cpms/excel/export_vulnerability_info.xls";
        // 本地测试
        String destXlsPath = "C:/data/yz1/excel/export_vulnerability_info.xls";

        File file = new File(destXlsPath);
        if (!file.getParentFile().exists()) {
            boolean mkdirs = file.getParentFile().mkdirs();
        }

        ExcelWriter writer = null;
        try {
            writer = EasyExcel.write(file).build();
            for (FlowOrVulnerabilityExportQueryParam queryParam : queryParams) {

                // 服务漏洞信息TOP10
                List<VulnerabilityCountInfoResponse> vulnerabilityCountInfo = getVulnerabilityCountInfo(queryParam);
                // 按CVSS划分漏洞
                List<VulnerabilityPieChartInfoExportResponse> cvss = getCVSS(queryParam);
                // 漏洞信息列表
                List<VulnerabilityInfoExportResponse> vulnerabilityInfo = getVulnerabilityInfo(queryParam);
                // 设置sheet页
                WriteSheet writeSheet = EasyExcel.writerSheet(queryParam.getName()).needHead(Boolean.FALSE).build();
                // 设置表头
                WriteTable writeTable1 = EasyExcel.writerTable(0).head(VulnerabilityCountInfoResponse.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable2 = EasyExcel.writerTable(1).head(VulnerabilityPieChartInfoExportResponse.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable3 = EasyExcel.writerTable(2).head(VulnerabilityInfoExportResponse.class).needHead(Boolean.TRUE).build();
                // 写入数据
                writer.write(vulnerabilityCountInfo, writeSheet, writeTable1);
                writer.write(cvss, writeSheet, writeTable2);
                writer.write(vulnerabilityInfo, writeSheet, writeTable3);
            }

        } catch (Exception e) {
            log.error("导出漏洞信息Excel失败", e);
            throw new MpRuntimeException("导出漏洞信息Excel失败");
        } finally {
            if (writer != null) {
                writer.finish();
            }
        }

        FileUtil.setResponse(file, true, response);

    }

    /**
     * 查询服务漏洞信息TOP10
     * @param queryParam
     * @return
     */
    private List<VulnerabilityCountInfoResponse> getVulnerabilityCountInfo(FlowOrVulnerabilityExportQueryParam queryParam) {

        ElementMonitorRequest pageRequest = new ElementMonitorRequest();
        pageRequest.setElementIds(Collections.singletonList(queryParam.getElementId()));
        pageRequest.setSceneId(queryParam.getSceneId());
        List<ElementVulnerabilityCountInfo> vulnerabilityCountInfo = sceneElementMonitorService.getVulnerabilityCountInfo(pageRequest);
        if (CollectionUtil.isEmpty(vulnerabilityCountInfo)) {
            return new ArrayList<>();
        }
        List<VulnerabilityCountInfoResponse> responses = new ArrayList<>();
        vulnerabilityCountInfo.forEach(e -> {
            VulnerabilityCountInfoResponse vulnerabilityCountInfoResponse = new VulnerabilityCountInfoResponse();
            BeanUtils.copyProperties(e, vulnerabilityCountInfoResponse);
            responses.add(vulnerabilityCountInfoResponse);
        });
        return responses;
    }

    /**
     * 按CVSS划分漏洞
     * @param queryParam
     * @return
     */
    private List<VulnerabilityPieChartInfoExportResponse> getCVSS(FlowOrVulnerabilityExportQueryParam queryParam) {

        ElementMonitorRequest pageRequest = new ElementMonitorRequest();
        pageRequest.setElementIds(Collections.singletonList(queryParam.getElementId()));
        pageRequest.setSceneId(queryParam.getSceneId());
        List<ElementVulnerabilityPieChartInfo> vulnerabilityPieChartInfo = sceneElementMonitorService.getVulnerabilityPieChartInfo(pageRequest);
        if (CollectionUtil.isEmpty(vulnerabilityPieChartInfo)) {
            return new ArrayList<>();
        }
        List<VulnerabilityPieChartInfoExportResponse> responses = new ArrayList<>();
        vulnerabilityPieChartInfo.forEach(e -> {
            VulnerabilityPieChartInfoExportResponse vulnerabilityPieChartInfoExportResponse = new VulnerabilityPieChartInfoExportResponse();
            vulnerabilityPieChartInfoExportResponse.setPercent(e.getPercent());
            vulnerabilityPieChartInfoExportResponse.setSeverity(getLevelName(e.getSeverity()));
            responses.add(vulnerabilityPieChartInfoExportResponse);
        });
        return responses;
    }
    private String getLevelName(String level) {
        Map<String , String> levelMap = new HashMap<>();
        levelMap.put("low", "低");
        levelMap.put("medium", "中");
        levelMap.put("high", "高");
        levelMap.put("critical", "极危");
        return levelMap.get(level);
    }

    /**
     * 漏洞列表
     * @param queryParam
     * @return
     */
    private List<VulnerabilityInfoExportResponse> getVulnerabilityInfo(FlowOrVulnerabilityExportQueryParam queryParam) {

        ElementMonitorRequest pageRequest = new ElementMonitorRequest();
        pageRequest.setElementIds(Collections.singletonList(queryParam.getElementId()));
        pageRequest.setSceneId(queryParam.getSceneId());
        pageRequest.setPage(1);
        pageRequest.setPerPage(999);
        ListObjResult<ElementVulnerabilityInfo> vulnerabilityInfo = sceneElementMonitorService.getVulnerabilityInfo(pageRequest);

        if (vulnerabilityInfo == null || CollectionUtils.isEmpty(vulnerabilityInfo.getList())) {
            return new ArrayList<>();
        }
        List<VulnerabilityInfoExportResponse> responses = new ArrayList<>();
        vulnerabilityInfo.getList().forEach(e -> {
            VulnerabilityInfoExportResponse vulnerabilityInfoExportResponse = new VulnerabilityInfoExportResponse();
            BeanUtils.copyProperties(e, vulnerabilityInfoExportResponse);
            vulnerabilityInfoExportResponse.setReferences(e.getReferences().toString());
            responses.add(vulnerabilityInfoExportResponse);
        });
        return responses;
    }


}
