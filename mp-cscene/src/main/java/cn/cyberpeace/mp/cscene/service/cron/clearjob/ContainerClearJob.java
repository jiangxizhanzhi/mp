 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerClearJob.java.
 // File size 2153.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.Container;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.ContainerGetData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ContainerClearJob
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 * @Version 1.0.0
 * @see AbstractClearJob
 */
public class ContainerClearJob extends AbstractClearJob<ContainerGetData> {

    private Container containerClient = new Container();

    /**
     * ContainerClearJob
     *
     * @param sceneElementMapper
     */
    public ContainerClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    /**
     * getEid2Node
     *
     * @param containers
     * @return
     */
    @Override
    protected Map<String, ContainerGetData> getEid2Node(List<ContainerGetData> containers) {
        Map<String, ContainerGetData> result = new HashMap<>();
        for (ContainerGetData container : containers) {
            if (container.getName() != null && container.getId() != null) {
                String eid = super.getEid(container.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, container);
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
    protected List<ContainerGetData> doQuery(int offset) {
        return containerClient.getAllContainer(LIMIT, offset);
    }

    /**
     * doClear
     *
     * @param data
     */
    @Override
    protected void doClear(ContainerGetData data) {
        containerClient.deleteContainerById(data.getId());
    }

    /**
     * forceClear
     *
     * @param data
     */
    @Override
    protected void forceClear(ContainerGetData data) {
        containerClient.deleteContainerById(data.getId());
    }
}
