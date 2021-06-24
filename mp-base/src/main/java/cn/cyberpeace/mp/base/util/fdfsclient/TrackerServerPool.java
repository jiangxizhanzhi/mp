package cn.cyberpeace.mp.base.util.fdfsclient;

import cn.cyberpeace.mp.base.config.FastDfsConfig;
import cn.cyberpeace.mp.base.constant.BaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerGroup;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * @Description: TrackerServer 对象池
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
@Slf4j
@Component
public class TrackerServerPool {

    private static FastDfsConfig fastDfsConfig;

    @Autowired
    public void setFastDfsConfig(FastDfsConfig fastDfsConfig) {
        TrackerServerPool.fastDfsConfig = fastDfsConfig;
    }

    /**
     * TrackerServer 对象池map
     */
    private static GenericObjectPool<TrackerServer> trackerServerPool;

    private static Map<String, GenericObjectPool<TrackerServer>> trackerServerPoolMap = new HashMap<>();

    public static Map<String, TrackerGroup> trackerGroupMap = new HashMap<>();

    private TrackerServerPool() {
    }

    /**
     * 初始化配置
     *
     * @return
     */
    private static Properties initConfigProps() {
        Properties props = new Properties();
        props.setProperty(ClientGlobal.PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS, fastDfsConfig.getConnectTimeoutInSeconds());
        props.setProperty(ClientGlobal.PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS, fastDfsConfig.getNetworkTimeoutInSeconds());
        props.setProperty(ClientGlobal.PROP_KEY_CHARSET, fastDfsConfig.getCharset());
        props.setProperty(ClientGlobal.PROP_KEY_HTTP_TRACKER_HTTP_PORT, fastDfsConfig.getHttpTrackerHttpPort());

        // 初始化trackerGroupMap
        List<FastDfsConfig.Group> groups = fastDfsConfig.getGroups();
        StringBuffer serversBuffer = new StringBuffer();
        for (int i = 0; i < groups.size(); i++) {
            FastDfsConfig.Group group = groups.get(i);
            String servers = group.getTrackerServers();
            trackerGroupMap.put(group.getGroupName(), initTrackerGroup(servers));

            // 用逗号拼接多个集群的TRACKER_SERVERS配置
            if (i == 0) {
                serversBuffer.append(servers);
            } else {
                serversBuffer.append(BaseConstants.COMMA).append(servers);
            }
        }

        // TRACKER_SERVERS配置
        props.setProperty(ClientGlobal.PROP_KEY_TRACKER_SERVERS, serversBuffer.toString());
        return props;
    }

    /**
     * 根据trackerServers初始化trackerGroupMap
     *
     * @param trackerServers
     * @return
     */
    private static TrackerGroup initTrackerGroup(String trackerServers) {
        List<InetSocketAddress> list = new ArrayList();
        String spr1 = BaseConstants.COMMA;
        String spr2 = BaseConstants.COLON;
        String[] arr1 = trackerServers.trim().split(spr1);
        for (String addrStr : arr1) {
            String[] arr2 = addrStr.trim().split(spr2);
            String host = arr2[0].trim();
            int port = Integer.parseInt(arr2[1].trim());
            list.add(new InetSocketAddress(host, port));
        }
        InetSocketAddress[] trackerAddresses = list.toArray(new InetSocketAddress[list.size()]);
        return new TrackerGroup(trackerAddresses);
    }

    /**
     * 获取 TrackerServer
     *
     * @return TrackerServer
     * @throws FastDFSException
     */
    public static TrackerServer borrowObject(String groupName) throws FastDFSException {
        TrackerServer trackerServer = null;
        try {
            trackerServer = getObjectPool(groupName).borrowObject();
        } catch (Exception e) {
            log.error("borrowObject error", e);
            if (e instanceof FastDFSException) {
                throw (FastDFSException) e;
            }
        }
        return trackerServer;
    }

    /**
     * 回收 TrackerServer
     *
     * @param trackerServer 需要回收的 TrackerServer
     */
    public static void returnObject(TrackerServer trackerServer, String groupName) {
        getObjectPool(groupName).returnObject(trackerServer);
    }

    /**
     * 初始化trackerServers
     *
     * @throws FastDFSException
     */
    public static void initTrackers(String trackerServers) throws FastDFSException {
        try {
            ClientGlobal.initByTrackers(trackerServers);
        } catch (Exception e) {
            log.error("initByTrackers error", e);
            throw new FastDFSException(ErrorCode.FILE_SERVER_INIT_FAILED.CODE,
                    ErrorCode.FILE_SERVER_INIT_FAILED.MESSAGE);
        }
    }

    private static synchronized GenericObjectPool<TrackerServer> getObjectPool(String groupName) {
        if (MapUtils.isEmpty(trackerServerPoolMap)) {
            try {
                // 加载配置文件
                Properties props = initConfigProps();
                ClientGlobal.initByProperties(props);
            } catch (Exception e) {
                log.error("加载FDFS配置文件失败", e);
            }

            log.info("ClientGlobal configInfo: {}", ClientGlobal.configInfo());

            // Pool配置
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMinIdle(2);
            if (fastDfsConfig.getMaxStorageConnection() > 0) {
                poolConfig.setMaxTotal(fastDfsConfig.getMaxStorageConnection());
            }

            // 初始化trackerServerPoolMap
            List<FastDfsConfig.Group> groups = fastDfsConfig.getGroups();
            groups.forEach(group -> {
                trackerServerPool = new GenericObjectPool<>(new TrackerServerFactory(group.getGroupName()), poolConfig);
                trackerServerPoolMap.put(group.getGroupName(), trackerServerPool);
            });

        }

        return trackerServerPoolMap.get(groupName);
    }

}
