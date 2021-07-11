package cn.cyberpeace.mp.aggregation.sceneimport.parse.export;

import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.AclsInfo;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.InitCommandParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NetElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.NodeElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementParseResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.SceneElementShape;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.StaticRoutesResult;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.NetworkElementParseResultV2;
import cn.cyberpeace.mp.aggregation.sceneimport.parse.result.v2.YmlParseResultV2;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.sceneconfig.SceneConfigHandler;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleAction;
import cn.cyberpeace.mp.sceneplugincloud.constant.FirewallRuleDirection;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class BaseSceneParseResourceCenter {

    public static String SPECIAL_STR_ON = "mp_special_str_on";

    public static String SPECIAL_STR_OFF = "mp_special_str_off";

    public static String SPECIAL_STR_EMPTY = "mp_special_str_empty";

    protected void handleNodeFile(List<NodeElementParseResult> nodes, Map<String, String> file2Download) {
        Map<String, Integer> fileName2Index = new HashMap<>();
        fileName2Index.put("file", 1);
        for (var node : nodes) {
            if (StringUtils.isNotEmpty(node.getFile())) {
                String file = node.getFile();
                
                int fileNameIndex = node.getFile().lastIndexOf("=");
                if (fileNameIndex > -1) {
                    node.setFile(StringUtils.substring(file, fileNameIndex, file.length()));
                    node.setFile(StringUtils.replace(node.getFile(), "=", ""));
                } else {
                    node.setFile("file");
                }
                if (fileName2Index.containsKey(node.getFile())) {
                    int index = fileName2Index.get(node.getFile());
                    node.setFile(index + node.getFile());
                    fileName2Index.put(node.getFile(), index + 1);
                } else {
                    fileName2Index.put(node.getFile(), 1);
                }
                file2Download.put(node.getFile(), file);
            }
        }
    }

    protected void handleNodeImage(SceneConfigHandler sceneHandler, Map<String, NodeElementParseResult> id2Node, List<Device> devices) {
        CloudSceneConfigPluginHandler pluginHandler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        Set<String> existImage = new HashSet<>();
        for (String nodeId : id2Node.keySet()) {
            var node = pluginHandler.getElement(nodeId);
            if (node != null) {
                if (!BaseCloudPluginSceneElementModel.ImageType.VM.equals(node.getImageType())
                        && !BaseCloudPluginSceneElementModel.ImageType.CONTAINER.equals(node.getImageType())) {
                    continue;
                }
                if (StringUtils.isEmpty(node.getImage())) {
                    continue;
                }
                if (!existImage.add(node.getImage())) {
                    continue;
                }
                Device device = new Device();
                device.setImageUuid(node.getImageUuid());
                device.setImage(node.getImage());
                device.setImageType(node.getImageType());
                device.setAccessModes(new ArrayList<>());
                
                if (BaseCloudPluginSceneElementModel.ImageType.VM.equals(node.getImageType())) {
                    device.setCpu(node.getSrcElement().getCpu());
                    device.setRam(node.getSrcElement().getRam());
                    device.setDisk(node.getSrcElement().getDisk());
                }
                devices.add(device);
                var element = sceneHandler.getElement(nodeId);
                device.setLogo(element.getIcon2());
                if (CollectionUtils.isNotEmpty(element.getPorts())) {
                    for (var p : element.getPorts()) {
                        if (CollectionUtils.isNotEmpty(p.getAccessModes())) {
                            device.getAccessModes().addAll(p.getAccessModes());
                        }
                    }
                }
            }
        }
    }

    @Getter
    @Setter
    public static class Result {
        private YmlParseResultV2 ymlResult;

        private Map<String, String> file2Download = new HashMap<>();

        private List<Device> devices = new ArrayList<>();

        private Map<String, String> idConvert = new HashMap<>();
    }

    @Setter
    @Getter
    public static class Device {
        private String image;
        private String imageType;
        private String logo;
        private String imageUuid;
        private Integer cpu;
        private Integer ram;
        private Integer disk;
        private List<AccessMode> accessModes;
    }

    @Getter
    @Setter
    protected static class Position {
        private Origin origin;
        private double height;
        private double width;
    }

    @Getter
    @Setter
    protected static class Origin {
        private double x;
        private double y;
    }
}
