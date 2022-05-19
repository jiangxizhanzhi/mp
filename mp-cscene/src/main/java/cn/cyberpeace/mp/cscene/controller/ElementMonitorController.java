package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.*;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.hutool.core.collection.CollectionUtil;
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

        return ServiceResult.success(elementMonitorBasicInfo);
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
        return ServiceResult.success(rtInfo);
    }


    @ApiOperation(value = "节点操作系统信息")
    @GetMapping("/os")
    public ServiceResult<OS> getOS(BaseSceneElementRequest baseRequest) {
        OS os = sceneElementMonitorService.getOSInfo(baseRequest);
        return ServiceResult.success(os);
    }


    @ApiOperation(value = "查询节点网络连接状态")
    @GetMapping("/connection_state")
    public ListObjServiceResult<ConnectionState> pageConnectionStates(BaseSceneElementPageRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        return sceneElementMonitorService.pageNetworkConnections(pageRequest);
    }

    @ApiOperation(value = "查询节点文件变更事件")
    @GetMapping("/file_modify")
    public ListObjServiceResult<CollectionHostEventBO> fileModify(ElementMonitorRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        return sceneElementMonitorService.getModifyData(pageRequest, CollectionUtil.toList("syscheck_file"));
    }

    @ApiOperation(value = "节点文件变更事件柱状图")
    @GetMapping("/file_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> fileModifyHistogram(ElementMonitorRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        return new ServiceResult<>(sceneElementMonitorService.getModifyHistogram(pageRequest, CollectionUtil.toList("syscheck_file")));
    }

    @ApiOperation(value = "查询节点注册表变更事件")
    @GetMapping("/registry_modify")
    public ListObjServiceResult<CollectionHostEventBO> registryModify(ElementMonitorRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        return sceneElementMonitorService.getModifyData(pageRequest, CollectionUtil.toList("syscheck_registry"));
    }

    @ApiOperation(value = "节点注册表变更事件柱状图")
    @GetMapping("/registry_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> registryModifyHistogram(ElementMonitorRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        return new ServiceResult<>(sceneElementMonitorService.getModifyHistogram(pageRequest, CollectionUtil.toList("syscheck_registry")));
    }

    @ApiOperation(value = "查询节点服务漏洞Top10")
    @GetMapping("/get_service_vulnerability")
    public ListObjServiceResult<ElementVulnerabilityCountInfo> getServiceVulnerability(BaseSceneElementPageRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();
        //mock数据
        List<ElementVulnerabilityCountInfo> vulnerabilityCountInfos = new ArrayList<>();
        List<String> types = new ArrayList<>(CollectionUtil.toList("VNC", "SNMP", "HTTP", "UPnP-HTTPU", "unknown", "ASF-RMCP", "SSH", "MySQL", "ASF", "HTTPS"));
        for (int i = 0; i < 10; i++) {
            ElementVulnerabilityCountInfo vulnerabilityCountInfo = new ElementVulnerabilityCountInfo();
            vulnerabilityCountInfo.setType(types.get(i));
            vulnerabilityCountInfo.setCount(i + 10);
            vulnerabilityCountInfos.add(vulnerabilityCountInfo);
        }

        return new ListObjServiceResult<>(new ListObjResult<>(vulnerabilityCountInfos, vulnerabilityCountInfos.size()));
    }

    @ApiOperation(value = "查询节点服务漏洞饼状图")
    @GetMapping("/get_vulnerability_pie_chart")
    public ListObjServiceResult<ElementVulnerabilityPieChartInfo> getVulnerabilityPieChart(BaseSceneElementPageRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();
        //mock数据
        List<ElementVulnerabilityPieChartInfo> vulnerabilityPieChartInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ElementVulnerabilityPieChartInfo vulnerabilityPieChartInfo = new ElementVulnerabilityPieChartInfo();
            vulnerabilityPieChartInfo.setLevel(i);
            vulnerabilityPieChartInfo.setPercent(6.7 * i);
            vulnerabilityPieChartInfos.add(vulnerabilityPieChartInfo);
        }

        return new ListObjServiceResult<>(new ListObjResult<>(vulnerabilityPieChartInfos, vulnerabilityPieChartInfos.size()));
    }

    @ApiOperation(value = "查询节点服务漏洞列表")
    @GetMapping("/get_vulnerability_list")
    public ListObjServiceResult<ElementVulnerabilityInfo> getVulnerabilityList(BaseSceneElementPageRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();
        //mock数据
        List<ElementVulnerabilityInfo> vulnerabilityInfos = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ElementVulnerabilityInfo vulnerabilityInfo = new ElementVulnerabilityInfo();
            vulnerabilityInfo.setNodeName("node_" + i);
            vulnerabilityInfo.setLevel(i);
            vulnerabilityInfo.setDescription("漏洞描述" + i);
            vulnerabilityInfo.setLogo("logo_" + i);
            vulnerabilityInfo.setTime(LocalDateTime.now());
            vulnerabilityInfos.add(vulnerabilityInfo);
        }
        for (int i = 0; i < 5; i++) {
            ElementVulnerabilityInfo vulnerabilityInfo = new ElementVulnerabilityInfo();
            vulnerabilityInfo.setNodeName("node_1" + i);
            vulnerabilityInfo.setLevel(i);
            vulnerabilityInfo.setDescription("漏洞影响" + i);
            vulnerabilityInfo.setLogo("logo_1" + i);
            vulnerabilityInfo.setTime(LocalDateTime.now());
            vulnerabilityInfos.add(vulnerabilityInfo);
        }
        for (int i = 0; i < 5; i++) {
            ElementVulnerabilityInfo vulnerabilityInfo = new ElementVulnerabilityInfo();
            vulnerabilityInfo.setNodeName("node_2" + i);
            vulnerabilityInfo.setLevel(i);
            vulnerabilityInfo.setDescription("漏洞影响描述" + i);
            vulnerabilityInfo.setLogo("logo_2" + i);
            vulnerabilityInfo.setTime(LocalDateTime.now());
            vulnerabilityInfos.add(vulnerabilityInfo);
        }

        return new ListObjServiceResult<>(new ListObjResult<>(vulnerabilityInfos, vulnerabilityInfos.size()));
    }

}
