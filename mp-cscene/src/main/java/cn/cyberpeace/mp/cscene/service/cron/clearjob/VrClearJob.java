package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterGetData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 */
public class VrClearJob extends AbstractClearJob<VirtualRouterGetData> {

    private VirtualRouter vrClient = new VirtualRouter();

    public VrClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    @Override
    protected Map<String, VirtualRouterGetData> getEid2Node(List<VirtualRouterGetData> vrs) {
        Map<String, VirtualRouterGetData> result = new HashMap<>();
        vrs.forEach(vr -> {
            if (vr.getName() != null && vr.getId() != null) {
                String eid = super.getEid(vr.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, vr);
                }
            }
        });
        return result;
    }

    @Override
    protected List<VirtualRouterGetData> doQuery(int offset) {
        return vrClient.getAllRouters(LIMIT, offset);
    }

    @Override
    protected void doClear(VirtualRouterGetData data) {
        vrClient.deleteRouterById(data.getId());
    }
}
