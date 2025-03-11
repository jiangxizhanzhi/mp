package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.multirange.SubRangeMonitorInterface;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.SceneElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneMapper;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Netiface;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.Process;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
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
    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;
    @Autowired
    private SceneElementMapper sceneElementMapper;

    @Override
    public String getIdentity(String currentCsceneId) {
        var csceneModels = csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getId, currentCsceneId).filterEqual(CsceneInstanceModel::getDeleted, false));
        if (CollectionUtils.isEmpty(csceneModels)) {
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("靶场csceneId"+currentCsceneId+"不存在"));
        } else {
            String currentSceneId = csceneModels.get(0).getScene();

            var sceneModels = sceneMapper.list(new HumanCondition().filterEqual(SceneModel::getId, currentSceneId).filterEqual(SceneModel::getDeleted, false)
                    .select(SceneModel::getServerVpnIp));
            if (CollectionUtils.isEmpty(sceneModels)) {
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("场景" + currentSceneId + "不存在"));
            } else {
                return sceneModels.get(0).getServerVpnIp();
            }
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
    public String getRangeMapCscene(String currentCsceneId) {

        String identity = getIdentity(currentCsceneId);
        String result = TestRangeClientUtil.get(String.format("%s?vpnServerIp=%s", TestRangeApiUrl.MAP_SCENE_ID, identity), null, getAuthHeaderMap(), 10000)
                .getResult();
        String mapCsceneId = JSONObject.parseObject(result).getString("data");
        if (StringUtils.isEmpty(mapCsceneId)) {
            log.warn("当前靶场没有配置mapSceneId，无法获取靶场元素信息");
            throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前靶场没有配置mapSceneId，无法获取靶场元素信息"));
        } else {
            return mapCsceneId;
        }
    }

    @Override
    public List<ElementMonitorBasicInfo> listAllElementBasicInfo(String csceneId, SceneAllElementsQueryRequest request) {

        String mapCsceneId = getRangeMapCscene(csceneId);
        String result = TestRangeClientUtil.get(String.format(TestRangeApiUrl.BASIC_ELEMENT_MONITOR_INFO, mapCsceneId), request, getAuthHeaderMap(), 10000)
                .getResult();
        JSONObject jsonObject = JSONObject.parseObject(result);
        List<ElementMonitorBasicInfo> elementMonitorBasicInfoList = jsonObject.getJSONArray("data").toJavaList(ElementMonitorBasicInfo.class);
        return elementMonitorBasicInfoList;
    }

    @Override
    public OS getElementOs(BaseSceneElementRequest baseRequest) {
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_OS, baseRequest, getAuthHeaderMap(), 10000)
                .getResult();
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getObject("data", OS.class);
    }

    @Override
    public ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_TREND_STATE, hostStateTrendRequest, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ServiceResult<List<HostState>>>() {
        });
    }

    @Override
    public ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest) {
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_STATE, pageRequest, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ServiceResult<List<HostState>>>() {
        });
    }

    @Override
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_NET_INTERFACE, listPageInfoQueryParams, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Netiface>>() {
        });
    }

    @Override
    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_PORT, listPageInfoQueryParams, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Port>>() {
        });
    }

    @Override
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_PROCESS, listPageInfoQueryParams, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Process>>() {
        });
    }


    public boolean elementExistInCurrentScene(BaseSceneElementRequest baseRequest) {
        String csceneInstanceId = baseRequest.getSceneId();
        String sceneInstanceId = csceneInstanceMapper.get(csceneInstanceId).getScene();
        var sceneElements = sceneElementMapper.list(new HumanCondition().filterEqual(SceneElementModel::getScene, sceneInstanceId).filterEqual(SceneElementModel::getNodeId, baseRequest.getElementIds().get(0)));
        if (CollectionUtils.isEmpty(sceneElements)) {
            log.info("当前节点{}不是当前靶场{}节点", baseRequest.getElementIds().get(0), baseRequest.getSceneId());
            return false;
        } else {
            return true;
        }
    }


}
