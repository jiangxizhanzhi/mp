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

    private static Set<String> existsNodeTask = new ConcurrentHashSet<>();

    public static void init() {
        if (!cacheInit) {
            taskPool = LinkStatusTaskPool.getInstance();
            CloudAppConfig config = BeanCommon.getBean(CloudAppConfig.class);
            int cacheSeconds = config.getLinkStatusCacheSeconds();
            log.info("init link status cache , cache times is {} seconds", cacheSeconds);
            //guava本地缓存创建
            cache = CacheBuilder.newBuilder()
                    //设置缓存
                    .maximumSize(300)
                    //对象多久不读就删除
                    .expireAfterWrite(cacheSeconds, TimeUnit.SECONDS)
                    .build(new CacheLoader<>() {
                        /**
                         * @description: 首次加载缓存（get不到对应的value时调用重写的load方法）
                         * @param sceneId
                         * @throws
                         * @return List<NodeLinkStatusBO>
                         */
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

    /**
     * @description: 清除缓存
     * @param sceneId
     * @throws
     * @return void
     */
    public static void clear(String sceneId) {
        try {
            cache.invalidate(sceneId);
        } catch (Exception e) {
            log.error("clear failed ,sceneId is {}", sceneId, e);
        }
    }

    /**
     * @description: 查询缓存
     * @param sceneId
     * @throws
     * @return List<NodeLinkStatusBO>
     */
    public static List<NodeLinkStatusBO> getCached(String sceneId) {
        return cache.getUnchecked(sceneId);
    }

    /**
     * @description: 刷新缓存
     * @param sceneId
     * @throws
     * @return void
     */
    public static void refresh(String sceneId) {
        cache.refresh(sceneId);
        // 10s之后再刷一次缓存 避免并发刷新时刷到旧的数据
        refreshExecutor.schedule(() -> {
            cache.refresh(sceneId);
        }, 10, TimeUnit.SECONDS);
    }

    /**
     * @description: 尝试开始刷新缓存任务
     *
     * @throws
     * @return void
     */
    public static void tryStartCronRefresh() {
        //是否开始刷新缓存
        if (!startCronRefresh) {
            var config = BeanCommon.getBean(CloudAppConfig.class);
            //刷新时间间隔
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

    /**
     * @description: 通过场景id或者对应所有节点的状态
     * @param sceneId 场景id
     * @throws
     * @return List<NodeLinkStatusBO>
     */
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

    /**
     * @description: 加载场景和节点状态信息关系
     * @param sceneId 场景id
     * @param nodeLinkStatusList 节点状态列表
     * @throws
     * @return void
     */
    private static void loadStatus(String sceneId, List<NodeLinkStatusBO> nodeLinkStatusList) {
        try {
            List<Runnable> tasks = new ArrayList<>();
            Remote remote = new Remote();
            for (var n : nodeLinkStatusList) {
                if (CollectionUtils.isNotEmpty(n.getConnectionStatusList())) {
                    for (var p : n.getConnectionStatusList()) {
                        String taskKey = getKey(n.getResourceId(), p.getNetworkId());
                        // 已有相同任务,则不处理
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
                //避免极端情况下 部分节点不刷新
                Collections.shuffle(tasks);
                taskPool.runTasks(tasks);
            }
        } catch (RejectedExecutionException e) {
            // do nothing
        } catch (Exception e) {
            log.error("failed to load status for scene {}, message is {}", sceneId, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * @description: 获取定时刷新任务
     *
     * @throws
     * @return Runnable
     */
    private static Runnable getCronRefreshTask() {
        return () -> {
            cache.asMap().entrySet().forEach(e -> {
                loadStatus(e.getKey(), e.getValue());
            });
        };
    }

    /**
     * @description: 通过资源id和网络id获得key
     * @param resourceId
     * @param networkId
     * @throws
     * @return String
     */
    private static String getKey(String resourceId, String networkId) {
        return StringUtils.joinWith("_", resourceId, networkId);
    }

    private static class LinkStatusTaskPool extends TaskPool {

        public static final LinkStatusTaskPool INSTANCE = new LinkStatusTaskPool();

        private LinkStatusTaskPool() {
            super(CloudSingleton.cloudAppConfig.getLinkStatusPoolSize(),
                    CloudSingleton.cloudAppConfig.getLinkStatusHoldSize(), 60L);
            //链接状态的任务太多即可考虑丢弃
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        }

        public static LinkStatusTaskPool getInstance() {
            return INSTANCE;
        }
    }
}
