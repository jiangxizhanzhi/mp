 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneNodeNumInitJob.java.
 // File size 813.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.job.base.init.MpInitiate;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName SceneNodeNumInitJob
 * @Description 初始化场景使用的节点数量
 * @Author maojun
 * @Date 2022/8/5
 * @Version 1.0.0
 */
@Component
public class SceneNodeNumInitJob {

    @Resource
    private CsceneService csceneService;

    @MpInitiate(id = "scene_node_num_init_job", description = "初始化刷新内存里的节点数量统计")
    public void execute() {
        int count = csceneService.getActiveVirtualDeviceCount();
        NodeLimitUtils.refreshCount(count);
    }
}
