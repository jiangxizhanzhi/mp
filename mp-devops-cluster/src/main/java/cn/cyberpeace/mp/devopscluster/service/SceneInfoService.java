package cn.cyberpeace.mp.devopscluster.service;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.hutool.core.collection.CollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/12/30 13:48
 */
@Service
public class SceneInfoService {

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    public void processSceneInfo(List<String> resourceNames, Consumer<Map<String, CsceneNodeInfoBO>> consumer) {
        // 添加场景信息
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(resourceNames);
        Map<String, CsceneNodeInfoBO> map = serviceResult.getSource();
        if (CollUtil.isNotEmpty(map)) {
            consumer.accept(map);
        }
    }

}
