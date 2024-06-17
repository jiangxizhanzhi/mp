package cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.scene.core.event.base.EventProcess;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.util.SystemTypeCommandUtils;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneAgentNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.bo.CloudPluginSceneElementNetworkBO;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineCreateData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineExecCmdData;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualNic;
import cn.cyberpeace.mp.sceneplugincloud.util.IpUtils;
import cn.cyberpeace.mp.scenepluginconfig.constant.ConfigSingleton;
import cn.cyberpeace.mp.scenepluginconfig.model.ConfigAgentModel;
import cn.cyberpeace.mp.scenepluginconfig.plugin.ConfigScenePluginHandler;
import cn.cyberpeace.mp.scenepluginconfig.plugin.lifecyclehandler.taskpool.CreateTaskPool;
import cn.cyberpeace.mp.scenepluginconfig.repository.ConfigAgentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.SpringApplication;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CreateHandler extends HandlerCommon {
    private final VirtualMachine vmClient = new VirtualMachine();
    private static final int AGENT_NETWORK_TREX_TYPE = 2;

    public static class SceneProcess {
        public static final EventProcess CREATE_AGENT = new EventProcess("create_agent", "scenepluginconfig.event.create_agent");
        public static final EventProcess START_CONFIG = new EventProcess("start_config", "scenepluginconfig.event.start_config");
        public static final EventProcess CREATE_VIRTUAL_MACHINE = new EventProcess("create_virtual_machine", "sceneplugincloud.event.create_virtual_machine");
    }

    public static class ElementProcess {
        public static final EventProcess START_CONFIG = new EventProcess("start_config", "scenepluginconfig.event.start_config");
    }


    public CreateHandler(ConfigScenePluginHandler pluginHandler) {
        super(pluginHandler, CreateTaskPool.getInstance(), SceneModel.Event.CREATE);
    }

    public void create() {
        prepareEventProcess(SceneModel.Event.CREATE, SceneProcess.class, ElementProcess.class);
        CloudScenePluginHandler cloudPluginHandler = getPluginHandler().getSceneHandler().getPluginHandler(PluginConstant.PLUGIN_KEY);

        // FIXME 在这里将场景拟制时设置的配置信息更新到配置命令中，有两种情况：
        // 1、dynamips模拟设备，在cpcloud的create阶段会生成IP地址、网段信息，这里需要转换为配置命令
        // 2、子网模型配置，会有物理对端口、VLAN、速率的设置，也需要转换为配置命令
        // 另一种方案：在界面上配置设备时，让用户指定关联的配置命令是哪个
        if (Boolean.TRUE.equals(ConfigSingleton.configAppConfig.getEnableConfigCreate())) {
            String sourceScene = getPluginHandler().getSourceSceneId();
            String configAgentTemplateId = ConfigSingleton.configAppConfig.getConfigAgentTemplateId();
            var configAgentMapper = BeanCommon.getBean(ConfigAgentMapper.class);
            List<Runnable> createConfigAgentTasks = new ArrayList<>();

            List<CloudPluginSceneAgentNetworkBO> agentNetworks = cloudPluginHandler.getAgentNetworks();
            AtomicInteger configTag = new AtomicInteger(0);

            var createVirtualEquipmentProcess = getPluginHandler().getSceneHandler().getSceneProcess().getCreateVirtualEquipment();
            createVirtualEquipmentProcess.addTotalCount((long) agentNetworks.size());

            for (CloudPluginSceneAgentNetworkBO agentNetwork : agentNetworks) {
                if (AGENT_NETWORK_TREX_TYPE == agentNetwork.getType()) {
                    continue;
                }
                var task = (Runnable) () -> {


                    var startTime = LocalDateTime.now();

                    String configAgentIp = agentNetwork.getConfigAgentIp();

                    VirtualMachineCreateData createData = new VirtualMachineCreateData();
                    createData.setName("CONFIG_" + sourceScene + "_" + configTag.addAndGet(1));
                    createData.setTemplateId(configAgentTemplateId);
                    createData.setCpu(4);
                    createData.setRam(1024 * ConfigSingleton.configAppConfig.getConfigAgentRam());
                    createData.setDisk(40);
                    createData.setMainCloud(true);
                    createData.setOsType(SystemTypeCommandUtils.OS_TYPE_LINUX);
                    List<VirtualNic> networks = new ArrayList<>();

                    VirtualNic externalNetwork = new VirtualNic();
                    externalNetwork.setNetworkId(agentNetwork.getResourceId());
                    externalNetwork.setGateway(agentNetwork.getGateway());
                    externalNetwork.setCidr(agentNetwork.getCidr());
                    externalNetwork.setFixedIp(configAgentIp);
                    networks.add(externalNetwork);


                    createData.setNetwork(networks);
                    createData.setCustomScript(getInitBashScript(configAgentIp));

                    VirtualMachineCreateData config;
                    long cloudConsumeSeconds;
                    try {
                        var cloudStartTime = LocalDateTime.now();
                        config = vmClient.create(createData);
                        cloudConsumeSeconds = Duration.between(cloudStartTime, LocalDateTime.now()).toSeconds();
                    } catch (Exception e) {
                        log.error("create config agent error, {}", ExceptionUtils.getStackTrace(e));
                        createVirtualEquipmentProcess.addFailCount(1L);
                        throw e;
                    }


                    ConfigAgentModel configAgentModel = new ConfigAgentModel();
                    configAgentModel.setLogicNetworkId(agentNetwork.getId());
                    configAgentModel.setName(config.getName());
                    configAgentModel.setResourceId(config.getId());
                    configAgentModel.setIp(configAgentIp);
                    configAgentModel.setNics(JsonCommon.stringify(Collections.emptyList()));
                    configAgentModel.setServer("http://" + configAgentModel.getIp() + ":8001");
                    configAgentModel.setCloudCreateConsumeSeconds(cloudConsumeSeconds);
                    configAgentModel.setExecCmdConsumeSeconds(0L);
                    configAgentModel.setCreateConsumeSeconds(Duration.between(startTime, LocalDateTime.now()).toSeconds());
                    configAgentMapper.create(configAgentModel);

                    getPluginHandler().getSceneHandler().addProgressPercentage(getPluginHandler().getStepPercentage());
                    createVirtualEquipmentProcess.addSuccessCount(1L);
                };
                createConfigAgentTasks.add(task);
            }

            if (CollectionUtils.isNotEmpty(createConfigAgentTasks)) {
                getPluginHandler().getSceneHandler().getCreateVirtualMachineTasks().addAll(createConfigAgentTasks);
            }
        }

        if (CollectionUtils.isNotEmpty(getPluginHandler().getSceneHandler().getCreateVirtualMachineTasks())) {
            var startTime = LocalDateTime.now();
            runSceneProgress(SceneProcess.CREATE_VIRTUAL_MACHINE, () ->
                    runTasks(new ArrayList<>(getPluginHandler().getSceneHandler().getCreateVirtualMachineTasks())));
            cloudPluginHandler.getScene().setCreateTerminalConsumeSeconds(Duration.between(startTime, LocalDateTime.now()).toSeconds());
        }

    }


    private String getInitBashScript(String externalIp) {
        String bashTag = "#!/bin/bash";
        String command = String.format("bash /home/config.sh %s %s %s %s %s %s", externalIp, ConfigSingleton.configAppConfig.getFrameHost(),
                TextUtils.uuid(), CloudSingleton.cloudAppConfig.getAgentWebDomain(), CloudSingleton.cloudAppConfig.getOcrDomain(), ConfigSingleton.configAppConfig.getTimeSyncIp());

        return String.join("\n", bashTag, command);
    }
}
