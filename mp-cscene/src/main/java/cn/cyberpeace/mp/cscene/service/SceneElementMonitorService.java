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
import cn.cyberpeace.mp.cscene.dto.scene.UserNodeSessionInfo;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.plugin.common.SystemTypeUtils;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.ListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.WazuhNetworkConnectionModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
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
    private CsceneCommonService csceneCommonService;

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
        ElementRTInfo rtInfo = processRTInfo(baseRequest);

        elementMonitorBasicInfo.setRtInfo(rtInfo);

        return elementMonitorBasicInfo;
    }



    public ListObjServiceResult<ConnectionState> pageNetworkConnections(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(pageRequest);
        if (!csceneCommonService.checkCsceneIsRunning(pageRequest.getSceneId())) {
            return new ListObjServiceResult(new ListObjResult()).asResult();
        }

        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
        listPageInfoQueryParams.setPage(pageRequest.getPage());
        listPageInfoQueryParams.setPageSize(pageRequest.getPageSize());
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollectionUtil.toList(pageRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(pageRequest.getElementIds());
        ListObjServiceResult<WazuhNetworkConnectionModel> netConns = scenePluginCollectionSceneService.listNetworkConnections(listPageInfoQueryParams).asResult();

        List<ConnectionState> connectionStates = new ArrayList<>();
        if (Objects.nonNull(netConns.getSource()) && CollectionUtil.isNotEmpty(netConns.getSource().getList())) {
            List<WazuhNetworkConnectionModel> list = netConns.getSource().getList();
            connectionStates = list.stream().map(model -> convertWazuhNetConn2ConnState(model)).collect(Collectors.toList());
        }

        return new ListObjServiceResult<>(new ListObjResult<>(connectionStates, netConns.getSource().getTotal()).asResult()).asResult();
    }

    public int sumEnvVarChangedCount(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
//        String sceneId = baseRequest.getSceneId();
//        String elementId = baseRequest.getElementIds().get(0);

        ListPageInfoQueryParams listPageInfoQueryParams = new ListPageInfoQueryParams();
//        listPageInfoQueryParams.setPage(pageRequest.getPage());
//        listPageInfoQueryParams.setPageSize(pageRequest.getPageSize());
        listPageInfoQueryParams.setSceneIds(csceneCommonService.translateSceneIds(CollectionUtil.toList(baseRequest.getSceneId())));
        listPageInfoQueryParams.setNodeIds(baseRequest.getElementIds());
        int sum = scenePluginCollectionSceneService.sumEnvVarChangedCount(listPageInfoQueryParams);
        return sum;
    }


    /**
     * 获取节点实时数据
     * @param baseRequest
     * @return
     */
    public ElementRTInfo getElementRTInfo(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        ElementRTInfo rtInfo = processRTInfo(baseRequest);

        return rtInfo;
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

            BaseSceneElementRequest request = new BaseSceneElementRequest();
            request.setSceneId(basicInfo.getSceneId());
            request.setElementIds(Arrays.asList(sceneElementBO.getId()));

            List<HostState> hostStates = hostStateService.getHostStates(request);
            if (CollectionUtil.isNotEmpty(hostStates)) {
                HostState hostState = hostStates.get(0);
                resource.setCpuCount(hostState.getCpuCount());
                resource.setMemoryTotal(hostState.getMemoryTotal());
                resource.setDiskSpace(hostState.getDiskSpace());

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

    /**
     * 处理节点实时信息，用户，进程，端口，网络连接
     * @param baseRequest
     * @return
     */
    private ElementRTInfo processRTInfo(BaseSceneElementRequest baseRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);
        ElementRTInfo rtInfo = new ElementRTInfo();

        // 用户
        rtInfo.setUserCount(countNodeSessionUsers(baseRequest));

        ListPageInfoQueryParams portsParams = new ListPageInfoQueryParams();
        portsParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        portsParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Port> portsResult = hostStateService.pagePorts(portsParams);

        // pagePorts内部有sceneId转换动作，这里pageProcesses不能共用一个param
        ListPageInfoQueryParams processParams = new ListPageInfoQueryParams();
        processParams.setSceneIds(CollectionUtil.toList(baseRequest.getSceneId()));
        processParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<Process> processesResult = hostStateService.pageProcesses(processParams);

        BaseSceneElementPageRequest pageRequest = new BaseSceneElementPageRequest();
        pageRequest.setSceneId(baseRequest.getSceneId());
        pageRequest.setElementIds(baseRequest.getElementIds());
        ListObjServiceResult<ConnectionState> conns = pageNetworkConnections(pageRequest);

        if (Objects.nonNull(conns.getSource().getTotal())) {
            // 网络连接
            rtInfo.setConnectionCount(conns.getSource().getTotal());
        }

        if (Objects.nonNull(portsResult.getSource().getTotal())) {
            // 端口
            rtInfo.setPortsCount(portsResult.getSource().getTotal());
        }

        if (Objects.nonNull(processesResult.getSource().getTotal())) {
            // 进程
            rtInfo.setProcessCount(processesResult.getSource().getTotal());
        }

        // 文件
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setNodeIds(baseRequest.getElementIds());
        ListObjServiceResult<CollectionHostEventBO> fileResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            // 文件数量包含其他所有类型，所以这里不会剔除注册表数量
           rtInfo.setFileOpCount(fileResult.getSource().getTotal());
        }

        // 注册表
        hostEventQueryParams.setRuleGroups(CollectionUtil.toList("syscheck_registry"));
        ListObjServiceResult<CollectionHostEventBO> registryResult = csceneInstanceService.listHostEvents(baseRequest.getSceneId(), hostEventQueryParams);
        if (CollectionUtil.isNotEmpty(fileResult.getSource().getObjs())) {
            // 注册表数量
            rtInfo.setRegistryOpCount(registryResult.getSource().getTotal());
        }

        // todo 文件操作，注册表变动，环境变量变动
        // todo 环境变量
        int envVarChangedCount = sumEnvVarChangedCount(baseRequest);
        rtInfo.setEnvOpCount(envVarChangedCount);

        return rtInfo;
    }

    /**
     * 统计节点当前登录用户数量
     * @param baseSceneElementRequest
     * @return
     */
    private int countNodeSessionUsers(BaseSceneElementRequest baseSceneElementRequest) {
        BaseRequestUtil.checkBaseRequestWithSceneId(baseSceneElementRequest);
        ServiceResult<List<UserNodeSessionInfo>> result = csceneInstanceService.getNodeSessions(CollectionUtil.toList(baseSceneElementRequest.getSceneId()), baseSceneElementRequest.getElementIds(), null, null);
        if (CollectionUtil.isNotEmpty(result.getSource())) {
            return result.getSource().size();
        } else {
            return 0;
        }
    }

}
