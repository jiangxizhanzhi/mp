package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualSwitch;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName NetClearJob
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 * @Version 1.0.0
 * @see AbstractClearJob
 */
public class NetClearJob extends AbstractClearJob<NetworkData> {

    private VirtualSwitch virtualSwitch = new VirtualSwitch();
    private Network netClient = new Network();

    /**
     * NetClearJob
     *
     * @param sceneElementMapper
     */
    public NetClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    /**
     * getEid2Node
     *
     * @param nets
     * @return
     */
    @Override
    protected Map<String, NetworkData> getEid2Node(List<NetworkData> nets) {
        Map<String, NetworkData> result = new HashMap<>();
        for (NetworkData net : nets) {
            if (net.getName() != null && net.getId() != null) {
                String eid = super.getEid(net.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, net);
                }
            }
        }
        return result;
    }

    /**
     * doQuery
     *
     * @param offset
     * @return
     */
    @Override
    protected List<NetworkData> doQuery(int offset) {
        return netClient.getAll(LIMIT, offset);
    }

    /**
     * doClear
     *
     * @param data
     */
    @Override
    protected void doClear(NetworkData data) {
        virtualSwitch.deleteById(data.getId());
    }

    /**
     * forceClear
     *
     * @param data
     */
    @Override
    protected void forceClear(NetworkData data) {

        virtualSwitch.deleteById(data.getId());
    }
}
