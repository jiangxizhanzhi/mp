package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.service.multirange.SubRangeMonitorInterface;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TestRangeApiProxy implements SubRangeMonitorInterface {

    @Autowired
    private TestRangeLoginData testRangeLoginData;
    @Autowired
    private SceneMapper sceneMapper;

    @Override
    public String getIdentity(String currentSceneId) {
        var sceneModels = sceneMapper.list(new HumanCondition().filterEqual(SceneModel::getId, currentSceneId).filterEqual(SceneModel::getDeleted, false)
                .select(SceneModel::getServerVpnIp));
        if (CollectionUtils.isEmpty(sceneModels)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("场景"+currentSceneId+"不存在"));
        } else {
            return sceneModels.get(0).getServerVpnIp();
        }
    }

    @Override
    public String getAuthorizationHeader() {
        return "JWT " + testRangeLoginData.getAuthToken();
    }

    public Map<String, String> getAuthHeaderMap() {
        String token = getAuthorizationHeader();
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", token);
        return headers;
    }


    @Override
    public String getRangeMapScene(String currentSceneId) {
        String identity = getIdentity(currentSceneId);
        String result = TestRangeClientUtil.get(String.format("%s?vpnServerIp=%s", TestRangeApiUrl.MAP_SCENE_ID, identity), null, getAuthHeaderMap(), 10000)
                .getResult();
        String mapSceneId = JSONObject.parseObject(result).getString("data");
        if (StringUtils.isEmpty(mapSceneId)) {
            log.warn("当前靶场没有配置mapSceneId，无法获取靶场元素信息");
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前靶场没有配置mapSceneId，无法获取靶场元素信息"));
        } else {
            return mapSceneId;
        }
    }

    @Override
    public List<ElementMonitorBasicInfo> listAllElementBasicInfo(String sceneId, SceneAllElementsQueryRequest request) {
        String mapSceneId = getRangeMapScene(sceneId);
        String result = TestRangeClientUtil.get(String.format(TestRangeApiUrl.BASIC_ELEMENT_MONITOR_INFO, mapSceneId), request, getAuthHeaderMap(), 10000)
                .getResult();
        JSONObject jsonObject = JSONObject.parseObject(result);
        List<ElementMonitorBasicInfo> elementMonitorBasicInfoList = jsonObject.getJSONArray("data").toJavaList(ElementMonitorBasicInfo.class);
        return elementMonitorBasicInfoList;
    }
}
