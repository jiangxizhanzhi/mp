package cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.constant.CommonSceneStage;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSceneStage;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.lifecyclehandler.taskpool.RecreateTaskPool;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/25
 */
@Slf4j
public class PrepareHandler extends HandlerCommon {


    public PrepareHandler(CloudScenePluginHandler pluginHandler) {
        super(pluginHandler, RecreateTaskPool.getInstance(), SceneElementModel.Event.INITIATE);
    }

    public void prepare(boolean partialStart) {
        var stageRecorder = getPluginHandler().getSceneHandler().getSceneStageRecorder();

        stageRecorder.addStage(CommonSceneStage.INIT);
        List<EventProcess> sceneEventProcesses = new ArrayList<>(ClassUtils.getClassConstantFieldMap(InitiateHandler.SceneProcess.class, EventProcess.class).values());
        stageRecorder.submitProcess(sceneEventProcesses);

        stageRecorder.addStage(CloudSceneStage.CREATE_NETWORK);
        stageRecorder.submitProcess(List.of(CreateHandler.SceneProcess.CREATE_AUXILIARY_NETWORKS));

        stageRecorder.addStage(CloudSceneStage.CREATE_GATEWAY);
        if (!getPluginHandler().getGatewayElements().isEmpty()) {
            stageRecorder.submitProcess(List.of(CreateHandler.SceneProcess.CREATE_GATEWAYS));
        }
        if (!getPluginHandler().getFirewallElements().isEmpty()) {
            stageRecorder.submitProcess(List.of(CreateHandler.SceneProcess.CREATE_FIREWALLS));
        }

        var terminals = getPluginHandler().getTerminalExcludeNpcElements();
        var npcs = getPluginHandler().getNpcElements();
        if (!terminals.isEmpty() || !npcs.isEmpty()) {
            stageRecorder.addStage(CloudSceneStage.CREATE_INSTANCE);
            if (!terminals.isEmpty()) {
                stageRecorder.submitProcess(List.of(CreateHandler.SceneProcess.CREATE_TERMINALS));
            }
            if (!npcs.isEmpty()) {
                stageRecorder.submitProcess(List.of(CreateHandler.SceneProcess.CREATE_NPCS));
            }
        }

        var dynamips = getPluginHandler().getDynamicElements();
        if (!dynamips.isEmpty()) {
            stageRecorder.addStage(CloudSceneStage.CONFIG);
            stageRecorder.submitProcess(List.of(CreateHandler.SceneProcess.CONFIGURE));
        }

        if (partialStart) {
            getPluginHandler().getElementMap().values().forEach(e -> {
                if (e.getSrcElement().getType() == BaseSceneElementModel.Type.TERMINAL) {
                    if (e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER || e.getVirtualType() == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE) {
                        getPluginHandler().getSceneHandler().addExcludeHandleNode(e.getSrcElement().getId());
                    }
                }
            });
        }
    }
}
