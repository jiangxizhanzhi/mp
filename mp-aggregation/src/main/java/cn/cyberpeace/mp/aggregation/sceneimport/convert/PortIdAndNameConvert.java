package cn.cyberpeace.mp.aggregation.sceneimport.convert;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/1/24
 */
public class PortIdAndNameConvert implements IConverter {

    @Override
    public void convert2BO(ConvertDataContainer container) {
        var cloudElementMap = container.getCloudPluginElements();
        if (CollectionUtils.isNotEmpty(container.getAllElements())) {
            container.getAllElements().forEach(e -> {
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    // 设置电口数量
                    CloudPluginSceneConfigElementBO cloudElement = cloudElementMap.get(e.getId());
                    cloudElement.setElectricPortNumber(e.getPorts().size());
                    // 设置网卡数量
                    e.setMaxPortNumber(e.getPorts().size());
                    // 如果是核心路由 设置wan口数量
                    Integer role = cloudElement.getRole();
                    if (role != null) {
                        if (role == BaseCloudPluginSceneElementModel.Role.VIRTUAL_LAYER_3_SIMPLE_ROUTER
                                    && BaseCloudPluginSceneElementModel.ImageType.VM.equals(cloudElement.getImageType())
                                    && Boolean.TRUE.equals(cloudElement.getIsBuildIn())
                        ) {
                            cloudElement.setWanPortNumber(e.getPorts().size());
                            cloudElement.setWanPortType("E");
                        }
                    }
                }
            });
        }
        if (CollectionUtils.isNotEmpty(container.getConnections())) {
            container.getConnections().forEach(conn -> {
                String source = conn.getSource();
                String target = conn.getTarget();
                Map<String, String[]> newName = new HashMap<>(4);
                newName.put("source", new String[]{ this.getNewName(source) });
                newName.put("target", new String[]{ this.getNewName(target) });
                conn.setName(JsonCommon.stringify(newName));
            });
        }
    }

    private String getNewName(String name) {
        int from = name.lastIndexOf("_");
        String suffix = name.substring(from + 1);
        if (name.contains("_electrical_")) {
            return "E0/" + suffix;
        } else {
            return "eth0/" + suffix;
        }
    }
}
