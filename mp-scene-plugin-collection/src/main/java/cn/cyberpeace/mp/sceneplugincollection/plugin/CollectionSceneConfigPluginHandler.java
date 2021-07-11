package cn.cyberpeace.mp.sceneplugincollection.plugin;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.core.plugin.stereotype.PluginKey;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudErrorCode;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudSceneConfigPluginHandler;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneConfigElementPortBO;
import cn.cyberpeace.mp.sceneplugincollection.bo.base.Probe;
import cn.cyberpeace.mp.sceneplugincollection.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincollection.constant.ProbeConstant;
import cn.cyberpeace.mp.sceneplugincollection.plugin.modelhandler.CollectionSceneConfigPluginModelHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ClassFieldClasses({CollectionPluginSceneConfigBO.class, CollectionPluginSceneConfigElementBO.class, CollectionPluginSceneConfigElementPortBO.class, Object.class,})
@Component
@PluginKey(value = PluginConstant.PLUGIN_KEY, priority = PluginConstant.PLUGIN_PRIORITY)
public class CollectionSceneConfigPluginHandler extends SceneConfigPluginHandler<CollectionPluginSceneConfigBO, CollectionPluginSceneConfigElementBO, CollectionPluginSceneConfigElementPortBO, Object> {

    private CollectionSceneConfigPluginModelHandler pluginModelHandler;

    @Override
    public void validate() {
        CloudSceneConfigPluginHandler cloudSceneConfigPluginHandler = getSceneHandler().getPluginHandler(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY);
        if (cloudSceneConfigPluginHandler != null) {
            for (Map.Entry<String, CloudPluginSceneConfigElementBO> entry : cloudSceneConfigPluginHandler.getElementMap().entrySet()) {
                var nodeId = entry.getKey();
                var node = entry.getValue();
                if (node.getVirtualType() != null && node.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER) {
                    
                    var connects = getElementConnectedSrcElements(nodeId);
                    if (CollectionUtils.isNotEmpty(connects) && connects.size() > 1) {
                        Set<String> connectMachineNodes = new HashSet<>();
                        for (var connectNode : connects) {
                            if (connectNode.getType() != null) {
                                if (connectNode.getType() == BaseSceneElementModel.Type.TERMINAL) {
                                    connectMachineNodes.add(connectNode.getId());
                                } else if (connectNode.getType() == BaseSceneElementModel.Type.SWITCH || connectNode.getType() == BaseSceneElementModel.Type.ROUTER) {
                                    List<String> machineNodes = this.getConnectMachineNodes(connectNode.getId());
                                    connectMachineNodes.addAll(machineNodes);
                                }
                            }
                        }
                        
                        connectMachineNodes.remove(nodeId);
                        int nums = 0;
                        for (String connectMachineNodeId : connectMachineNodes) {
                            if (getElementMap().containsKey(connectMachineNodeId)) {
                                nums++;
                            }
                        }
                        if (nums > 1) {
                            throw new MpRuntimeException(CloudErrorCode.UN_SUPPORT_MULTI_NET);
                        }
                    }
                }
            }
        }
    }

    private List<String> getConnectMachineNodes(String nodeId) {
        List<String> results = new ArrayList<>();
        var connects = getElementConnectedSrcElements(nodeId);
        for (var connectNode : connects) {
            if (connectNode.getType() != null) {
                if (connectNode.getType() == BaseSceneElementModel.Type.TERMINAL) {
                    results.add(connectNode.getId());
                }
            }
        }
        return results;
    }

    @Override
    public void correctData() {
        
        if (getElementMap() != null && !getElementMap().isEmpty()) {
            getElementMap().values().forEach(e -> {
                if (e.getProbes() == null || e.getProbes().isEmpty()) {
                    return;
                }
                e.getProbes().forEach(probe -> {
                    
                    if (probe.getScriptUrl() != null) {
                        probe.setExecType(Probe.ExecType.SCRIPT_PROBE);
                    } else if (ProbeConstant.ProbeId.REGISTRY_MODIFY.equals(probe.getId())) {
                        
                        probe.setExecType(Probe.ExecType.REGISTRY_PROBE);
                    } else if (ProbeConstant.ProbeId.FILE_MODIFY.equals(probe.getId())) {
                        
                        probe.setExecType(Probe.ExecType.FILE_PROBE);
                    } else if (probe.getCommon() && ProbeConstant.ProbeType.LOG_TYPE.equals(probe.getType())) {
                        
                        probe.setExecType(Probe.ExecType.LOG_PROBE);
                    } else {
                        
                        probe.setExecType(Probe.ExecType.BASE_PROBE);
                    }
                });
            });
        }
    }

    @Override
    public void initBase() {
        super.initBase();
        pluginModelHandler = new CollectionSceneConfigPluginModelHandler(this);
    }

    @Override
    public void reloadData() {
        setScene(null);
        getElementMap().clear();
        getElementPortMap().clear();

        pluginModelHandler.loadScene(pluginModelHandler.readScene());
        pluginModelHandler.loadElements(pluginModelHandler.readElements(getElementIds().toArray(new String[0])));
        pluginModelHandler.loadElementPorts(pluginModelHandler.readElementPorts(getElementPortIds().toArray(new String[0])));
    }

    @Override
    public void saveScene() {
        pluginModelHandler.saveScene();
    }

    @Override
    public void deleteScene() {
        pluginModelHandler.deleteScene();
    }

    @Override
    public void createElements(String... elementIds) {
        pluginModelHandler.createElements(elementIds);
    }

    @Override
    public void updateElements(String... elementIds) {
        pluginModelHandler.updateElements(elementIds);
    }

    @Override
    public void deleteElements(String... elementIds) {
        pluginModelHandler.deleteElements(elementIds);
    }

    @Override
    public void createElementPorts(String... elementPortIds) {
        pluginModelHandler.createElementPorts(elementPortIds);
    }

    @Override
    public void updateElementPorts(String... elementPortIds) {
        pluginModelHandler.updateElementPorts(elementPortIds);
    }

    @Override
    public void deleteElementPorts(String... elementPortIds) {
        pluginModelHandler.deleteElementPorts(elementPortIds);
    }

}
