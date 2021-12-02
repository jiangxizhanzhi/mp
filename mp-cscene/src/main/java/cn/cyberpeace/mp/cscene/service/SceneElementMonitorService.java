package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementPort;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.util.BaseRequestUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.cyberpeace.mp.sceneplugincollection.service.ScenePluginCollectionSceneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ScenePluginCollectionSceneService scenePluginCollectionSceneService;

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
        processTopologyElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);
        processPluginElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);

        return elementMonitorBasicInfo;
    }


    public ElementRTInfo getElementRTInfo(BaseSceneElementRequest baseRequest) {
        log.info("baseRequest:{}", baseRequest);
        BaseRequestUtil.checkBaseRequestWithSceneIdAndElementIds(baseRequest);

        // todo 1201 实时状态

        return null;
    }


    public void processTopologyElement(ElementMonitorBasicInfo basicInfo, CsceneInstanceBO csceneInstanceBO, String elementId) {
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
            sceneElementBO2ElementMonitorBasicInfo(basicInfo, tpoEleBO);
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
            CloudPluginSceneElementBO elementBO = JsonCommon.snakeMapToBean(pluginEleOptional.get(), CloudPluginSceneElementBO.class);
            cpSceneElementBO2ElementMonitorBasicInfo(basicInfo, elementBO);
        }
    }


    public void sceneElementBO2ElementMonitorBasicInfo(ElementMonitorBasicInfo basicInfo, SceneElementBO sceneElementBO) {
        HostState resource = basicInfo.getHostState();
        if (Objects.isNull(resource)) {
            resource = new HostState();
        }

        resource.setCpuCount(Optional.ofNullable(sceneElementBO.getCpu()).orElse(0));

        // 内存大小
        Integer memSize = Optional.ofNullable(sceneElementBO.getRam()).orElse(0);
//        double memSizeInGb = NumberUtil.round(memSizeInMb / 1024.0, 2).doubleValue();
        resource.setMemoryTotal(memSize);

        // 磁盘大小
        Integer diskSize = Optional.ofNullable(sceneElementBO.getDisk()).orElse(0);
        resource.setDiskSpace(diskSize);
        basicInfo.setHostState(resource);

        // ip地址
//        List<String> ipList = sceneElementBO.getPorts().stream().map(BaseSceneElementPortBO::getIp).collect(Collectors.toList());
//        basicInfo.setIps(ipList);

        // ID
        basicInfo.setId(sceneElementBO.getId());
        // 实例名称
        basicInfo.setName(sceneElementBO.getName());

        // 操作系统
        // 参考SystemTypeUtils
        basicInfo.setSystemType(sceneElementBO.getSystemType());


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

}
