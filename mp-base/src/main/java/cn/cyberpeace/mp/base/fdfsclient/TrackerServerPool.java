package cn.cyberpeace.mp.base.fdfsclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Description: TrackerServer 对象池
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
@Slf4j
public class TrackerServerPool {
    /**
     * TrackerServer 配置文件路径
     */
    private static final String FASTDFS_CONFIG_PATH = "application.properties";

    /**
     * 最大连接数 default 8.
     */
    @Value("${max_storage_connection}")
    private static int maxStorageConnection;

    /**
     * TrackerServer 对象池.
     * GenericObjectPool 没有无参构造
     */
    private static GenericObjectPool<TrackerServer> trackerServerPool;

    private TrackerServerPool() {
    }

    private static synchronized GenericObjectPool<TrackerServer> getObjectPool() {
        if (trackerServerPool == null) {
            try {
                // 加载配置文件
                ClientGlobal.initByProperties(FASTDFS_CONFIG_PATH);
            } catch (Exception e) {
                log.error("加载FDFS配置文件事变");
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


}
