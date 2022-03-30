package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Network;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.NetworkData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 */
public class NetClearJob extends AbstractClearJob<NetworkData> {

    private Network netClient = new Network();

    public NetClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    @Override
    protected Map<String, NetworkData> getEid2Node(List<NetworkData> nets) {
        Map<String, NetworkData> result = new HashMap<>();
        nets.forEach(net -> {
            if (net.getName() != null && net.getId() != null) {
                String eid = super.getEid(net.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, net);
                }
            }
        });
        return result;
    }

    @Override
    protected List<NetworkData> doQuery(int offset) {
        return netClient.getAll(LIMIT, offset);
    }

    @Override
    protected void doClear(NetworkData data) {
        netClient.deleteNetworkById(data.getId());
    }
}
