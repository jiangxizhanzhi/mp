 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneInfoService.java.
 // File size 1283.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @ClassName SceneInfoService
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/12/30 13:48
 * @Version 1.0.0
 */
@Service
public class SceneInfoService {

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    /**
     * processSceneInfo
     *
     * @param resourceNames
     * @param consumer
     */
    public void processSceneInfo(List<String> resourceNames, Consumer<Map<String, CsceneNodeInfoBO>> consumer) {
        // 添加场景信息
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(resourceNames);
        Map<String, CsceneNodeInfoBO> map = serviceResult.getSource();
        if (MapUtils.isNotEmpty(map)) {
            consumer.accept(map);
        }
    }

}
