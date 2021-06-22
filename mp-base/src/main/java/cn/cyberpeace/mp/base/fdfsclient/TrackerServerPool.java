package cn.cyberpeace.mp.base.fdfsclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Properties;

/**
 * @Description: TrackerServer 对象池
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
@Slf4j
@Component
public class TrackerServerPool {

    /**
     * 最大连接数
     */
    private static int maxStorageConnection;

    private static String connectTimeoutInSeconds;

    private static String networkTimeoutInSeconds;

    private static String charset;

    private static String httpTrackerHttpPort;

    @Value("${fastdfs.maxStorageConnection}")
    public void setMaxStorageConnection(int maxStorageConnection) {
        TrackerServerPool.maxStorageConnection = maxStorageConnection;
    }

    @Value("${fastdfs.connectTimeoutInSeconds}")
    public void setConnectTimeoutInSeconds(String connectTimeoutInSeconds) {
        TrackerServerPool.connectTimeoutInSeconds = connectTimeoutInSeconds;
    }

    @Value("${fastdfs.networkTimeoutInSeconds}")
    public void setNetworkTimeoutInSeconds(String networkTimeoutInSeconds) {
        TrackerServerPool.networkTimeoutInSeconds = networkTimeoutInSeconds;
    }

    @Value("${fastdfs.charset}")
    public void setCharset(String charset) {
        TrackerServerPool.charset = charset;
    }

    @Value("${fastdfs.httpTrackerHttpPort}")
    public void setHttpTrackerHttpPort(String httpTrackerHttpPort) {
        TrackerServerPool.httpTrackerHttpPort = httpTrackerHttpPort;
    }

    /**
     * TrackerServer 对象池.
     * GenericObjectPool 没有无参构造
     */
    private static GenericObjectPool<TrackerServer> trackerServerPool;

    private TrackerServerPool() {
    }

    /**
     * 初始化配置
     * @return
     */
    private static Properties initConfigProps() {
        Properties props = new Properties();
        props.setProperty(ClientGlobal.PROP_KEY_CONNECT_TIMEOUT_IN_SECONDS, connectTimeoutInSeconds);
        props.setProperty(ClientGlobal.PROP_KEY_NETWORK_TIMEOUT_IN_SECONDS, networkTimeoutInSeconds);
        props.setProperty(ClientGlobal.PROP_KEY_CHARSET, charset);
        props.setProperty(ClientGlobal.PROP_KEY_HTTP_TRACKER_HTTP_PORT, httpTrackerHttpPort);

        return props;
    }

    private static synchronized GenericObjectPool<TrackerServer> getObjectPool() {
        if (trackerServerPool == null) {
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
            if (maxStorageConnection > 0) {
                poolConfig.setMaxTotal(maxStorageConnection);
            }

            trackerServerPool = new GenericObjectPool<>(new TrackerServerFactory(), poolConfig);
        }
        return trackerServerPool;
    }

    /**
     * 获取 TrackerServer
     *
     * @return TrackerServer
     * @throws FastDFSException
     */
    public static TrackerServer borrowObject() throws FastDFSException {
        TrackerServer trackerServer = null;
        try {
            trackerServer = getObjectPool().borrowObject();
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
    public static void returnObject(TrackerServer trackerServer) {
        getObjectPool().returnObject(trackerServer);
    }


    /**
     * 初始化trackerServers
     * @throws FastDFSException
     */
    public static void initTrackers(String trackerServers) throws FastDFSException {
        try {
            ClientGlobal.initByTrackers(trackerServers);
        } catch (Exception e) {
            log.error("initByTrackers error", e);
            throw new FastDFSException(ErrorCode.FILE_SERVER_INIT_FAILED.CODE, ErrorCode.FILE_SERVER_INIT_FAILED.MESSAGE);
        }
    }

}
