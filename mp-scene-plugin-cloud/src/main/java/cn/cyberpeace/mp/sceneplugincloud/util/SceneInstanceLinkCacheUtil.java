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
import cn.hutool.core.collection.ConcurrentHashSet;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


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

    private static Set<String> existsNodeTask = new ConcurrentHashSet<>();

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
        try {
            List<Runnable> tasks = new ArrayList<>();
            Remote remote = new Remote();
            for (var n : nodeLinkStatusList) {
                if (CollectionUtils.isNotEmpty(n.getConnectionStatusList())) {
                    for (var p : n.getConnectionStatusList()) {
                        String taskKey = getKey(n.getResourceId(), p.getNetworkId());
                        
                        if (!existsNodeTask.contains(taskKey)) {
                            tasks.add(() -> {
                                try {
                                    existsNodeTask.add(taskKey);
                                    LinkStatusParams params = new LinkStatusParams();
                                    params.setInstanceId(n.getResourceId());
                                    params.setNetworkId(p.getNetworkId());
                                    if (StringUtils.isEmpty(params.getInstanceId())) {
                                        n.setStatus(OFFLINE);
                                        p.setStatus(OFFLINE);
                                    } else {
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
                                    }
                                } finally {
                                    existsNodeTask.remove(taskKey);
                                }
                            });
                        }
                    }
                } else if (StringUtils.isNotEmpty(n.getDefaultNetworkId())) {
                    String taskKey = getKey(n.getResourceId(), n.getDefaultNetworkId());
                    if (!existsNodeTask.contains(taskKey)) {
                        tasks.add(() -> {
                            try {
                                existsNodeTask.add(taskKey);
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
                            } finally {
                                existsNodeTask.remove(taskKey);
                            }
                        });
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(tasks)) {
                
                Collections.shuffle(tasks);
                taskPool.runTasks(tasks);
            }
        } catch (RejectedExecutionException e) {
            
        } catch (Exception e) {
            log.error("failed to load status for scene {}, message is {}", sceneId, ExceptionUtils.getStackTrace(e));
        }
    }

    private static Runnable getCronRefreshTask() {
        return () -> {
            cache.asMap().entrySet().forEach(e -> {
                loadStatus(e.getKey(), e.getValue());
            });
        };
    }

    private static String getKey(String resourceId, String networkId) {
        return StringUtils.joinWith("_", resourceId, networkId);
    }

    private static class LinkStatusTaskPool extends TaskPool {

        public static final LinkStatusTaskPool INSTANCE = new LinkStatusTaskPool();

        private LinkStatusTaskPool() {
            super(CloudSingleton.cloudAppConfig.getLinkStatusPoolSize(),
                    CloudSingleton.cloudAppConfig.getLinkStatusHoldSize(), 60L);
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }

        public static LinkStatusTaskPool getInstance() {
            return INSTANCE;
        }
    }
}
