 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VrClearJob.java.
 // File size 2081.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.service.cron.clearjob;

import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualRouter;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualRouterGetData;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName VrClearJob
 * @Description
 * @Author maojun
 * @Date 2022/3/24
 * @Version 1.0.0
 * @see AbstractClearJob
 */
public class VrClearJob extends AbstractClearJob<VirtualRouterGetData> {

    private VirtualRouter vrClient = new VirtualRouter();

    /**
     * VrClearJob
     *
     * @param sceneElementMapper
     */
    public VrClearJob(SceneElementMapper sceneElementMapper) {
        super(sceneElementMapper);
    }

    /**
     * getEid2Node
     *
     * @param vrs
     * @return
     */
    @Override
    protected Map<String, VirtualRouterGetData> getEid2Node(List<VirtualRouterGetData> vrs) {
        Map<String, VirtualRouterGetData> result = new HashMap<>();
        for (VirtualRouterGetData vr : vrs) {
            if (vr.getName() != null && vr.getId() != null) {
                String eid = super.getEid(vr.getName());
                if (StringUtils.isNotEmpty(eid)) {
                    result.put(eid, vr);
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
    protected List<VirtualRouterGetData> doQuery(int offset) {
        return vrClient.getAllRouters(LIMIT, offset);
    }

    /**
     * doClear
     *
     * @param data
     */
    @Override
    protected void doClear(VirtualRouterGetData data) {
        vrClient.deleteRouterById(data.getId());
    }

    /**
     * forceClear
     *
     * @param data
     */
    @Override
    protected void forceClear(VirtualRouterGetData data) {
        vrClient.deleteRouterById(data.getId());
    }
}
