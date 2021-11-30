package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.BaseSceneElementPortBO;
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

//    @Autowired
//    private CsceneInstanceService c

    /**
     * CsceneInstanceBO转为ElementMonitorInfo
     *
     * @return
     */
    public ElementMonitorBasicInfo getElementMonitorInfo(String sceneId, String elementId) {
        log.info("sceneId:{}, elementId:{}", sceneId, elementId);
        var result = csceneInstanceService.getById(sceneId, null);
        var csceneInstanceBO = result.getSource();

        ElementMonitorBasicInfo elementMonitorBasicInfo = new ElementMonitorBasicInfo();
        processTopologyElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);
        processPluginElement(elementMonitorBasicInfo, csceneInstanceBO, elementId);

        return elementMonitorBasicInfo;
    }


    public ElementRTInfo getElementRTInfo(String sceneId, String elementId) {
        log.info("sceneId:{}, elementId:{}", sceneId, elementId);


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
        List<String> ipList = sceneElementBO.getPorts().stream().map(BaseSceneElementPortBO::getIp).collect(Collectors.toList());
        basicInfo.setIps(ipList);

        // 实例名称
        basicInfo.setName(sceneElementBO.getName());

        // 操作系统
        // 参考SystemTypeUtils
        basicInfo.setSystemType(sceneElementBO.getSystemType());


        // 用户
        /*var ports = sceneElementBO.getPorts();
        for (SceneElementPortBO port : ports) {

        }*/
        // 登录方式

        basicInfo.setPorts(sceneElementBO.getPorts());

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
