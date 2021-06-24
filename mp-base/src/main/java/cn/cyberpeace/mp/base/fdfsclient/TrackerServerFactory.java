package cn.cyberpeace.mp.base.fdfsclient;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

/**
 * @Description: TrackerServer 工厂类，创建对象池需要 BasePooledObjectFactory 对象或子类
 * @Autho qiaokang
 * @Date：2021/6/21 15:01
 */
public class TrackerServerFactory extends BasePooledObjectFactory<TrackerServer> {

    /**
     * fdfs集群名
     */
    private String groupName;

    public TrackerServerFactory(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public TrackerServer create() throws Exception {
        // TrackerClient:根据集群名称，传入对应的trackerGroup
        TrackerClient trackerClient = new TrackerClient(TrackerServerPool.trackerGroupMap.get(groupName));
        // TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();

        return trackerServer;
    }

    @Override
    public PooledObject<TrackerServer> wrap(TrackerServer trackerServer) {
        return new DefaultPooledObject<TrackerServer>(trackerServer);
    }
}
