package cn.cyberpeace.mp.sceneplugincloud.util;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.util.thread.taskpool.TaskPool;
import cn.cyberpeace.mp.scene.core.bo.SceneElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.sceneplugincloud.bo.NodeLinkStatusBO;
import cn.cyberpeace.mp.sceneplugincloud.config.CloudAppConfig;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudSingleton;
import cn.cyberpeace.mp.sceneplugincloud.constant.CloudUrlConstant;
import cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginHandler;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Remote;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.LinkStatusParams;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.LinkStatusResultData;
import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/1
 */
@Slf4j
public class SceneInstanceLinkCacheUtil {

    private static int ONLINE = 1;

    private static int OFFLINE = 0;

    private static LoadingCache<String, List<NodeLinkStatusBO>> cache = null;

    private static volatile boolean startCronRefresh = false;

    private static volatile boolean cacheInit = false;

    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    private static ScheduledThreadPoolExecutor refreshExecutor = new ScheduledThreadPoolExecutor(1);

    private static LinkStatusTaskPool taskPool;

    public static void init() {
        if (!cacheInit) {
            taskPool = LinkStatusTaskPool.getInstance();
            CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
            int cacheSeconds = config.getLinkStatusCacheSeconds();
            log.info("init link status cache , cache times is {} seconds", cacheSeconds);
            cache = CacheBuilder.newBuilder()
                    .maximumSize(300)
                    .expireAfterWrite(cacheSeconds, TimeUnit.SECONDS)
                    .build(new CacheLoader<>() {
                        @Override
                        public List<NodeLinkStatusBO> load(String sceneId) {
                            var nodeLinkStatusList = getNodeLinkStatusList(sceneId);
                            loadStatus(sceneId, nodeLinkStatusList);
                            return nodeLinkStatusList;
                        }
                    });
            cacheInit = true;
        }
    }

    public static void clear(String sceneId) {
        try {
            cache.invalidate(sceneId);
        } catch (Exception e) {
            log.error("clear failed ,sceneId is {}", sceneId, e);
        }
    }

    public static List<NodeLinkStatusBO> getCached(String sceneId) {
        return cache.getUnchecked(sceneId);
    }

    public static void refresh(String sceneId) {
        cache.refresh(sceneId);
        // 10s之后再刷一次缓存 避免并发刷新时刷到旧的数据
        refreshExecutor.schedule(() -> {
            cache.refresh(sceneId);
        }, 10, TimeUnit.SECONDS);
    }

    public static void tryStartCronRefresh() {
        if (!startCronRefresh) {
            var config = BeanCommon.getBean(CloudAppConfig.class);
            int linkStatusRefreshPeriod = config.getLinkStatusRefreshPeriod();
            log.info("init link status cache , cache times is {} seconds", linkStatusRefreshPeriod);
            try {
                executor.scheduleAtFixedRate(getCronRefreshTask(), 1, config.getLinkStatusRefreshPeriod(), TimeUnit.SECONDS);
                startCronRefresh = true;
            } catch (Exception e) {
                log.error("failed to start schedule task for refresh link status");
            }
        }
    }

    private static List<NodeLinkStatusBO> getNodeLinkStatusList(String sceneId) {
        SceneHandler sceneHandler = new SceneHandler(sceneId, null, null);
        CloudScenePluginHandler handler = sceneHandler.getPluginHandler(PluginConstant.PLUGIN_KEY);
        List<NodeLinkStatusBO> nodeLinkStatusList = new ArrayList<>();
        sceneHandler.getElementMap().values().forEach(e -> {
            var cloudElement = handler.getElement(e.getId());
            Integer virtualType = cloudElement.getVirtualType();
            if (virtualType != null && (
                    virtualType == BaseCloudPluginSceneElementModel.VirtualType.VIRTUAL_MACHINE ||
                            virtualType == BaseCloudPluginSceneElementModel.VirtualType.CONTAINER
            )) {
                NodeLinkStatusBO nodeStatus = new NodeLinkStatusBO(e.getId());
                nodeStatus.setResourceId(handler.getElement(e.getId()).getResourceId());
                nodeLinkStatusList.add(nodeStatus);
                if (CollectionUtils.isNotEmpty(e.getPorts())) {
                    for (var p : e.getPorts()) {
                        if (StringUtils.isEmpty(nodeStatus.getDefaultNetworkId())) {
                            // 设置一个默认网络,如果没有连线,则使用该网络查询状态
                            var cloudPort = handler.getPort(p.getId());
                            if (cloudPort != null && cloudPort.getAuxiliaryNetworkId() != null) {
                                nodeStatus.setDefaultNetworkId(cloudPort.getAuxiliaryNetworkId());
                            }
                        }
                        var connectPort = sceneHandler.getPortIdConnectedPortIdMap().get(p.getId());
                        if (connectPort != null) {
                            String connectionId = null;
                            String key = StringUtils.joinWith(":", p.getId(), connectPort);
                            SceneElementPortConnectionBO connection = sceneHandler.getElementPortConnectionMap().get(key);
                            if (connection == null) {
                                key = StringUtils.joinWith(":", connectPort, p.getId());
                                connection = sceneHandler.getElementPortConnectionMap().get(key);
                            }
                            if (connection != null) {
                                NodeLinkStatusBO.ConnectionStatus connectionStatus = new NodeLinkStatusBO.ConnectionStatus(connection.getId());
                                connectionStatus.setNetworkId(handler.getPort(p.getId()).getAuxiliaryNetworkId());
                                nodeStatus.getConnectionStatusList().add(connectionStatus);
                            }
                        }
                    }
                }
            }
        });
        return nodeLinkStatusList;
    }

    private static void loadStatus(String sceneId, List<NodeLinkStatusBO> nodeLinkStatusList) {
        List<Runnable> tasks = new ArrayList<>();
        Remote remote = new Remote();
        for (var n : nodeLinkStatusList) {
            if (CollectionUtils.isNotEmpty(n.getConnectionStatusList())) {
                for (var p : n.getConnectionStatusList()) {
                    tasks.add(() -> {
                        LinkStatusParams params = new LinkStatusParams();
                        params.setInstanceId(n.getResourceId());
                        params.setNetworkId(p.getNetworkId());
                        if (CloudSingleton.cloudAppConfig.getLinkStatusEnabled()) {
                            var result = remote.cloudGet(CloudUrlConstant.LINK_STATUS, params, new TypeReference<CloudResponse<LinkStatusResultData>>() {
                            }, true);
                            if (result.isSuccess()) {
                                n.setStatus(Boolean.TRUE.equals(result.getData().getInstanceStatus()) ? ONLINE : OFFLINE);
                                p.setStatus(Boolean.TRUE.equals(result.getData().getInstanceLinkStatus()) ? ONLINE : OFFLINE);
                            } else {
                                log.error("failed to get link status , sceneId is {} , result is {}", sceneId, JsonCommon.stringify(result));
                            }
                        } else {
                            n.setStatus(ONLINE);
                            p.setStatus(ONLINE);
                        }
                    });
                }
            } else if (StringUtils.isNotEmpty(n.getDefaultNetworkId())) {
                tasks.add(() -> {
                    LinkStatusParams params = new LinkStatusParams();
                    params.setInstanceId(n.getResourceId());
                    params.setNetworkId(n.getDefaultNetworkId());
                    if (CloudSingleton.cloudAppConfig.getLinkStatusEnabled()) {
                        var result = remote.cloudGet(CloudUrlConstant.LINK_STATUS, params, new TypeReference<CloudResponse<LinkStatusResultData>>() {
                        }, true);
                        if (result.isSuccess()) {
                            n.setStatus(Boolean.TRUE.equals(result.getData().getInstanceStatus()) ? ONLINE : OFFLINE);
                        } else {
                            log.error("failed to get link status , sceneId is {} , result is {}", sceneId, JsonCommon.stringify(result));
                        }
                    } else {
                        n.setStatus(ONLINE);
                    }
                });
            }
        }
        taskPool.runTasks(tasks);
    }

    private static Runnable getCronRefreshTask() {
        return () -> {
            cache.asMap().entrySet().forEach(e -> {
                loadStatus(e.getKey(), e.getValue());
            });
        };
    }

    private static class LinkStatusTaskPool extends TaskPool {

        public static final LinkStatusTaskPool INSTANCE = new LinkStatusTaskPool();

        private LinkStatusTaskPool() {
            super(CloudSingleton.cloudAppConfig.getLinkStatusPoolSize(),
                    CloudSingleton.cloudAppConfig.getLinkStatusHoldSize(), 60L);
        }

        public static LinkStatusTaskPool getInstance() {
            return INSTANCE;
        }
    }
}
