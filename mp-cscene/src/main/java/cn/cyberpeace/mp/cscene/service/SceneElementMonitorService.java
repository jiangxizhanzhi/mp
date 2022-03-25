package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementPort;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.scene.SceneStateInfo;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Description: 场景节点监控Service
 * @Author zhouxinghuai
 * @Date：2021/11/23 14:45
 */
@Slf4j
@Service
public class SceneElementMonitorService {

    private static final String CPCLOUD_PLUGIN = "cpcloud";

    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    @Autowired
    private SceneTranslationService sceneTranslationService;

    /**
     * CsceneInstanceBO转为ElementMonitorInfo
     *
     * @return
     */
    public ElementMonitorBasicInfo getElementMonitorInfo(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        String sceneId = baseRequest.getSceneId();
        String elementId = baseRequest.getElementIds().get(0);

        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        ElementMonitorBasicInfo elementMonitorBasicInfo = new ElementMonitorBasicInfo();
        elementMonitorBasicInfo.setSceneId(sceneId);
        processTopologyElement(elementMonitorBasicInfo, csceneInstanceBO, elementId, true);
        processPluginElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);

        // todo 节点实时信息
        ElementRTInfo rtInfo = new ElementRTInfo();
        rtInfo.setUserCount(0);
        rtInfo.setProcessCount(0);
        rtInfo.setPortsCount(0);
        rtInfo.setConnectionCount(0);

        elementMonitorBasicInfo.setRtInfo(rtInfo);

        return elementMonitorBasicInfo;
    }

    public ListObjServiceResult<ConnectionState> pageNetworkConnections(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);
        if (!checkCsceneIsRunning(pageRequest.getSceneId())) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }

        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setPage(pageRequest.getPage());
        listPageInfoQueryParams.setPageSize(pageRequest.getPageSize());
        listPageInfoQueryParams.setSceneIds(sceneTranslationService.translateSceneIds(CollectionUtil.toList(pageRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(pageRequest.getElementIds());
        ListObjServiceResult<WazuhNetworkConnectionModel> netConns = scenePluginCollectionSceneService.listNetworkConnections(listPageInfoQueryParams).asResult();

        List<ConnectionState> connectionStates = new ArrayList<>();
        if (Objects.nonNull(netConns.getSource()) && CollectionUtil.isNotEmpty(netConns.getSource().getList())) {
            List<WazuhNetworkConnectionModel> list = netConns.getSource().getList();
            connectionStates = list.stream().map(model -> convertWazuhNetConn2ConnState(model)).collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(connectionStates, netConns.getSource().getTotal()).asResult()).asResult();
    }


    public ElementRTInfo getElementRTInfo(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);

        // todo 1201 实时状态

        return null;
    }


    public void processTopologyElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId, boolean fetchRealTimeHostState) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getTopology())) {
            return;
        }

        var topology = csceneInstanceBO.getScene().getTopology();
        var topologyElements = topology.getElements();
        Optional<SceneElementBO> tpoEleOptional = topologyElements.stream().filter(e -> Objects.equals(elementId, e.getId())).findFirst();

        if (tpoEleOptional.isPresent()) {
            SceneElementBO tpoEleBO = tpoEleOptional.get();
            sceneElementBO2ElementMonitorBasicInfo(basicInfo, tpoEleBO, csceneInstanceBO, fetchRealTimeHostState);
        }
    }

    public void processPluginElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins())
                || Objects.isNull(csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN))) {
            return;
        }

        var scenePluginDataBO = csceneInstanceBO.getScene().getPlugins().get(CPCLOUD_PLUGIN);
        var pluginElements = scenePluginDataBO.getElements();
        var pluginEleOptional = pluginElements.entrySet().stream().filter(entry -> Objects.equals(entry.getKey(), elementId)).map(Map.Entry::getValue).findFirst();

        if (pluginEleOptional.isPresent()) {
            CloudPluginSceneElementBO cloudPluginSceneElementBO = JsonCommon.snakeMapToBean(pluginEleOptional.get(), CloudPluginSceneElementBO.class);
            cpSceneElementBO2ElementMonitorBasicInfo(basicInfo, cloudPluginSceneElementBO);
        }
    }


    public void sceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, SceneElementBO sceneElementBO, CsceneInstanceBO csceneInstanceBO, boolean fetchRealTimeHostState) {
        log.info("fetchHostState:{}", fetchRealTimeHostState);
        // 获取hostState比较耗时，这里加个开关
        if (fetchRealTimeHostState) {
            HostState resource = basicInfo.getHostState();
            if (Objects.isNull(resource)) {
                resource = new HostState();
            }

            resource.setCpuCount(Optional.ofNullable(sceneElementBO.getCpu()).orElse(0));

            // 内存大小
            Integer memSize = Optional.ofNullable(sceneElementBO.getRam()).orElse(0);
            //        double memSizeInGb = NumberUtil.round(memSizeInMb / 1024.0, 2).doubleValue();
            resource.setMemoryTotal(Long.valueOf(memSize));

            // 磁盘大小
            Integer diskSize = Optional.ofNullable(sceneElementBO.getDisk()).orElse(0);
            resource.setDiskSpace(Long.valueOf(diskSize));

            BaseSceneElementRequest request = new BaseSceneElementRequest();
            request.setSceneId(basicInfo.getSceneId());
            request.setElementIds(Arrays.asList(sceneElementBO.getId()));

            List<HostState> hostStates = hostStateService.getHostStates(request);
//            List<HostState> hostStates = hostStateService.getHostStatesFromWazuh(basicInfo.getSceneId(), sceneElementBO.getEid(), csceneInstanceBO.getCreateTime(), LocalDateTime.now());
            if (CollectionUtil.isNotEmpty(hostStates)) {
                HostState hostState = hostStates.get(0);
                resource.setCpuPercent(Objects.nonNull(hostState.getCpuPercent()) ? NumberUtil.round(hostState.getCpuPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryPercent(Objects.nonNull(hostState.getMemoryPercent()) ? NumberUtil.round(hostState.getMemoryPercent(), 2).doubleValue() : 0.0);
                resource.setMemoryFree(hostState.getMemoryFree());
                resource.setDiskPercent(Objects.nonNull(hostState.getDiskPercent()) ? NumberUtil.round(hostState.getDiskPercent(), 2).doubleValue() : 0.0);
            }
            basicInfo.setHostState(resource);
        }

        // ip地址
//        List<String> ipList = sceneElementBO.getPorts().stream().map(BaseSceneElementPortBO::getIp).collect(Collectors.toList());
//        basicInfo.setIps(ipList);

        // ID
        basicInfo.setId(sceneElementBO.getId());
        basicInfo.setEid(sceneElementBO.getEid());
        // 实例名称
        basicInfo.setName(sceneElementBO.getName());

        // 操作系统
        // 参考SystemTypeUtils
        if (StringUtils.isNotBlank(sceneElementBO.getSystemType())) {
            basicInfo.setSystemType(sceneElementBO.getSystemType());
            basicInfo.setSystemTypeName(SystemTypeUtils.getStandardSystemType(sceneElementBO.getSystemType()));
        }

        // ip, 用户名，登录方式
        var ports = sceneElementBO.getPorts();
        List<ElementPort> ePorts = ports.stream().map(p -> {
            ElementPort port = new ElementPort();
            port.setIp(p.getIp());
            port.setName(p.getName());
            port.setAccessModes(p.getAccessModes());
            return port;
        }).collect(Collectors.toList());
        basicInfo.setPorts(ePorts);

    }

    public void cpSceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, CloudPluginSceneElementBO cpSceneElementBO) {
        // 镜像名称
        basicInfo.setImageName(cpSceneElementBO.getImage());

        // 物理节点
        basicInfo.setHost(cpSceneElementBO.getHost());

        // 实例类型
        basicInfo.setVirtualType(cpSceneElementBO.getVirtualType());
    }


    private boolean checkCsceneIsRunning(String csceneId) {
        ServiceResult<SceneStateInfo> result = csceneSpecialService.getSceneStateInfo(csceneId);
        SceneStateInfo sceneStateInfo = result.getSource();
        return sceneStateInfo.isRunning();
    }

    private ConnectionState convertWazuhNetConn2ConnState(WazuhNetworkConnectionModel model) {
        ConnectionState connectionState = new ConnectionState();
        connectionState.setState(model.getStatus());
        connectionState.setProtocol(model.getProto());
        connectionState.setPid(model.getPid());

        connectionState.setLocalAddress(model.getLaddrIp());
        connectionState.setLocalPort(model.getLaddrPort());

        connectionState.setForeignAddress(model.getRaddrIp());
        connectionState.setForeignPort(model.getRaddrPort());

        return connectionState;
    }
}
