package cn.cyberpeace.mp.cscene.service.multirange;

import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;

import java.util.List;

/**
 * 子靶场态势监控相关接口
 *
 */
public interface SubRangeMonitorInterface {
    String getIdentity(String currentSceneId);

    String getAuthorizationHeader();

    String getRangeMapScene(String identity);

    List<ElementMonitorBasicInfo> listAllElementBasicInfo(String rangeMapScene, SceneAllElementsQueryRequest request);

}
