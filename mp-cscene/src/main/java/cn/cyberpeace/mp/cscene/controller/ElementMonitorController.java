package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.*;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "文件路径 模糊查询", dataTypeClass = String.class),
    })
    @GetMapping("/file_modify")
    public ListObjServiceResult<CollectionHostEventBO> fileModify(ElementMonitorRequest pageRequest, @RequestParam(required = false) String path) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        return sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP, path).asResult();
    }

    @ApiOperation(value = "节点文件变更事件柱状图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "segmentCount", value = "分段数，默认15", dataTypeClass = Integer.class),
    })
    @GetMapping("/file_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> fileModifyHistogram(@RequestParam(required = false) Integer segmentCount,
                                                                         ElementMonitorRequest pageRequest) {
        return new ServiceResult<>(sceneElementMonitorService.getModifyHistogram(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP,
                segmentCount)).asResult();
    }

    @ApiOperation(value = "查询节点注册表变更事件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "注册表路径 模糊查询", dataTypeClass = String.class),
    })
    @GetMapping("/registry_modify")
    public ListObjServiceResult<CollectionHostEventBO> registryModify(ElementMonitorRequest pageRequest, @RequestParam(required = false) String path) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        return sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.REGISTRY_MODIFY_GROUP, path).asResult();
    }

    @ApiOperation(value = "节点注册表变更事件柱状图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "segmentCount", value = "分段数，默认15", dataTypeClass = Integer.class),
    })
    @GetMapping("/registry_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> registryModifyHistogram(@RequestParam(required = false) Integer segmentCount,
                                                                             ElementMonitorRequest pageRequest) {
        return new ServiceResult<>(sceneElementMonitorService.getModifyHistogram(pageRequest, CsceneBizConstant.REGISTRY_MODIFY_GROUP,
                segmentCount)).asResult();
    }

    @ApiOperation(value = "查询节点服务漏洞Top10")
    @GetMapping("/get_service_vulnerability")
    public ListObjServiceResult<ElementVulnerabilityCountInfo> getServiceVulnerability(ElementMonitorRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        var result = sceneElementMonitorService.getVulnerabilityCountInfo(pageRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }

    @ApiOperation(value = "查询节点服务漏洞饼状图")
    @GetMapping("/get_vulnerability_pie_chart")
    public ListObjServiceResult<ElementVulnerabilityPieChartInfo> getVulnerabilityPieChart(ElementMonitorRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        var result = sceneElementMonitorService.getVulnerabilityPieChartInfo(pageRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }

    @ApiOperation(value = "查询节点服务漏洞列表")
    @GetMapping("/get_vulnerability_list")
    public ListObjServiceResult<ElementVulnerabilityInfo> getVulnerabilityList(ElementMonitorRequest pageRequest) {
        log.info("{}", pageRequest);
        pageRequest.ensure();

        var result = sceneElementMonitorService.getVulnerabilityInfo(pageRequest);
        return new ListObjServiceResult<>(result).asResult();
    }

}
