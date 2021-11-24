package cn.cyberpeace.mp.cscene.service;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.dto.event.ElementMonitorInfo;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementBO;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
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


    /**
     * CsceneInstanceBO转为ElementMonitorInfo
     *
     * @param csceneInstanceBO
     * @return
     */
    public ElementMonitorInfo sceneInstanceBO2ElementMonitorInfo(CsceneInstanceBO csceneInstanceBO, String elementId) {
        log.info("sceneId:{}, elementId:{}", csceneInstanceBO.getId(), elementId);

        ElementMonitorInfo elementMonitorInfo = new ElementMonitorInfo();
        processTopologyElement(elementMonitorInfo, csceneInstanceBO, elementId);
        processPluginElement(elementMonitorInfo, csceneInstanceBO, elementId);

        return elementMonitorInfo;
    }

    private void processTopologyElement(ElementMonitorInfo elementMonitorInfo, CsceneInstanceBO csceneInstanceBO, String elementId) {
        if (Objects.isNull(csceneInstanceBO)
                || Objects.isNull(csceneInstanceBO.getScene())
                || Objects.isNull(csceneInstanceBO.getScene().getTopology())) {
        }

        var topology = csceneInstanceBO.getScene().getTopology();
        var topologyElements = topology.getElements();
        Optional<SceneElementBO> tpoEleOptional = topologyElements.stream().filter(e -> Objects.equals(elementId, e.getId())).findFirst();

        if (tpoEleOptional.isPresent()) {
            SceneElementBO tpoEleBO = tpoEleOptional.get();
            elementMonitorInfo.setNodeCPUs(Optional.ofNullable(tpoEleBO.getCpu()).orElse(0));

            // 内存大小，原单位MB 转化为 GB
            int memSizeInMb = Optional.ofNullable(tpoEleBO.getRam()).orElse(0);
            double memSizeInGb = NumberUtil.round(memSizeInMb / 1024.0, 2).doubleValue();
            elementMonitorInfo.setMemSize(memSizeInGb);

            // 磁盘大小，原单位GB
            int diskInGb = Optional.ofNullable(tpoEleBO.getDisk()).orElse(0);
            elementMonitorInfo.setDiskSize(diskInGb);

            // ip地址
            List<String> ipList = tpoEleBO.getPorts().stream().map(p -> p.getIp()).collect(Collectors.toList());
            elementMonitorInfo.setIps(ipList);

            // 实例名称
            elementMonitorInfo.setName(tpoEleBO.getName());

            // 操作系统
            // 参考SystemTypeUtils
            elementMonitorInfo.setSystemType(tpoEleBO.getSystemType());

        }
    }

    private void processPluginElement(ElementMonitorInfo elementMonitorInfo, CsceneInstanceBO csceneInstanceBO, String elementId) {
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

            // 镜像名称
            elementMonitorInfo.setImageName(elementBO.getImage());

            // todo 物理节点 待确认

            // todo 实例类型 待确认
            elementMonitorInfo.setInstanceType(elementBO.getVirtualType());

        }
    }


}
