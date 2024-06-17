package cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineDeleteData;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigAgentModel;
import cn.cyberpeace.mp.scenepluginconfig.plugin.ConfigScenePluginHandler;
import cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.taskpool.DeleteTaskPool;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigAgentMapper;
import cn.cyberpeace.mp.task.model.CommonAgentRegistryModel;
import cn.cyberpeace.mp.task.repository.CommonAgentRegistryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DeleteHandler extends HandlerCommon {

    public static class SceneProcess {
        public static final EventProcess DELETE_AGENT = new EventProcess("delete_agent", "scenepluginconfig.event.delete_agent");
        public static final EventProcess DELETE_CONFIG = new EventProcess("delete_config", "scenepluginconfig.event.delete_config");
    }

    public static class ElementProcess {
        public static final EventProcess DELETE_CONFIG = new EventProcess("delete_config", "scenepluginconfig.event.delete_config");
    }

    public DeleteHandler(ConfigScenePluginHandler pluginHandler) {
        super(pluginHandler, DeleteTaskPool.getInstance(), SceneModel.Event.DELETE);
    }

    public void delete() {
        Runnable deleteAgentTask = () -> {
            log.info("delete agent task start");
            try {
                ConfigAgentMapper configAgentMapper = BeanCommon.getBean(ConfigAgentMapper.class);
                CommonAgentRegistryMapper agentRegistryMapper = BeanCommon.getBean(CommonAgentRegistryMapper.class);
                List<ConfigAgentModel> configAgentModels = configAgentMapper.list(new HumanCondition()
                        .filterContains(ConfigAgentModel::getName, getHandlerUtils().getPluginHandler().getSourceSceneId()));
                if (CollectionUtils.isNotEmpty(configAgentModels)) {
                    List<String> vmIds = configAgentModels.stream().map(ConfigAgentModel::getResourceId).collect(Collectors.toList());
                    VirtualMachineDeleteData deleteData = new VirtualMachineDeleteData();
                    deleteData.setVms(vmIds);
                    try {
                        new VirtualMachine().delete(deleteData);
                    } catch (Exception e) {
                        log.error("delete config agent error,params:{}", JsonCommon.stringify(deleteData));
                    }
                    configAgentMapper.bulkDelete(configAgentModels.stream().map(ConfigAgentModel::getId).toArray(String[]::new));
                    agentRegistryMapper.filterDelete(new HumanCondition()
                            .filterIn(CommonAgentRegistryModel::getIp, configAgentModels.stream().map(ConfigAgentModel::getIp).toArray()));
                    getPluginHandler().getSceneHandler().addProgressPercentage(getPluginHandler().getStepPercentage() * vmIds.size());
                }
            } catch (Exception e) {
                log.error("delete agent task error:{}", ExceptionUtils.getStackTrace(e));
            }
            log.info("delete agent task end");
        };
        getPluginHandler().getSceneHandler().getDeleteVirtualMachineTasks().add(deleteAgentTask);
    }

}
