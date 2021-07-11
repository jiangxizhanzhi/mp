package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerGetData;
import cn.cyberpeace.mp.sceneplugincloud.util.ForceReleaseResourceUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContainerClearJob extends AbstractClearJob<ContainerGetData> {

    private Container containerClient = new Container();

    public ContainerClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    @Override
    protected Map<String, ContainerGetData> getEid2Node(List<ContainerGetData> containers) {
        Map<String, ContainerGetData> result = new HashMap<>();
        containers.forEach(container -> {
            if (container.getName() != null && container.getId() != null) {
                String eid = super.getEid(container.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, container);
                }
            }
        });
        return result;
    }

    @Override
    protected List<ContainerGetData> doQuery(int offset) {
        return containerClient.getAllContainer(LIMIT, offset);
    }

    @Override
    protected void doClear(ContainerGetData data) {
        containerClient.deleteContainerById(data.getId());
    }

    @Override
    protected void forceClear(ContainerGetData data) {
        ForceReleaseResourceUtils.forceDeleteContainer(Arrays.asList(data.getId()));
    }
}
