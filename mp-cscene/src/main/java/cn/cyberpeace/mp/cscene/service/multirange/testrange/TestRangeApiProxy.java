package cn.cyberpeace.mp.cscene.service.multirange.testrange;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.dto.flow.FlowAttackDto;
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
import cn.cyberpeace.mp.sceneplugincollection.service.param.FlowQueryParams;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

    private static final Map<String, String> CSCENE_MAP_CACHE = new ConcurrentHashMap<>();

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
        if (CSCENE_MAP_CACHE.containsKey(currentCsceneId)) {
            return CSCENE_MAP_CACHE.get(currentCsceneId);
        } else {
            String identity = getIdentity(currentCsceneId);
            String result = TestRangeClientUtil.get(String.format("%s?vpnServerIp=%s", TestRangeApiUrl.MAP_SCENE_ID, identity), null, getAuthHeaderMap(), 10000)
                    .getResult();
            String mapCsceneId = JSONObject.parseObject(result).getString("data");
            if (StringUtils.isEmpty(mapCsceneId)) {
                log.warn("当前靶场没有配置mapSceneId，无法获取靶场元素信息");
                throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("当前靶场没有配置mapSceneId，无法获取靶场元素信息"));
            } else {
                CSCENE_MAP_CACHE.put(currentCsceneId, mapCsceneId);
                return mapCsceneId;
            }
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
        String mapCsceneId = getRangeMapCscene(baseRequest.getSceneId());
        baseRequest.setSceneId(mapCsceneId);
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_OS, baseRequest, getAuthHeaderMap(), 10000)
                .getResult();
        JSONObject jsonObject = JSONObject.parseObject(result);
        return jsonObject.getObject("data", OS.class);
    }

    @Override
    public ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        String mapCsceneId = getRangeMapCscene(hostStateTrendRequest.getSceneId());
        hostStateTrendRequest.setSceneId(mapCsceneId);
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_TREND_STATE, hostStateTrendRequest, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ServiceResult<List<HostState>>>() {
        });
    }

    @Override
    public ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest) {
        String mapCsceneId = getRangeMapCscene(pageRequest.getSceneId());
        pageRequest.setSceneId(mapCsceneId);
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_STATE, pageRequest, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ServiceResult<List<HostState>>>() {
        });
    }

    @Override
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        String mapCsceneId = getRangeMapCscene(listPageInfoQueryParams.getSceneIds().get(0));
        listPageInfoQueryParams.setSceneIds(List.of(mapCsceneId));
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_NET_INTERFACE, listPageInfoQueryParams, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Netiface>>() {
        });
    }

    @Override
    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        String mapCsceneId = getRangeMapCscene(listPageInfoQueryParams.getSceneIds().get(0));
        listPageInfoQueryParams.setSceneIds(List.of(mapCsceneId));
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_PORT, listPageInfoQueryParams, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Port>>() {
        });
    }

    @Override
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        String mapCsceneId = getRangeMapCscene(listPageInfoQueryParams.getSceneIds().get(0));
        listPageInfoQueryParams.setSceneIds(List.of(mapCsceneId));
        String result = TestRangeClientUtil.get(TestRangeApiUrl.ELEMENT_PROCESS, listPageInfoQueryParams, getAuthHeaderMap(), 10000).getResult();
        return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Process>>() {
        });
    }

    @Override
    public List<FlowAttackDto> pageFlow(String id, FlowQueryParams queryParams) {
         List<String> targetNodeIds = queryParams.getTargetNodeIds();
         if (CollectionUtils.isEmpty(targetNodeIds)) {
             return new ArrayList<>();
         }
         Set<String> allTargetNodes = new HashSet<>(queryParams.getTargetNodeIds());
         Set<String> existElements = elementsExistInCurrentScene(id, allTargetNodes);
         allTargetNodes.removeAll(existElements);
         String mapCsceneId = getRangeMapCscene(id);
         queryParams.setTargetNodeIds(new ArrayList<>(allTargetNodes));
         queryParams.setId(mapCsceneId);
         queryParams.setPage(1);
         queryParams.setPerPage(10000);

         String result = TestRangeClientUtil.get(TestRangeApiUrl.FLOW_ATTACK_PAGE, queryParams, getAuthHeaderMap(), 10000).getResult();
         var resultList = JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<FlowAttackDto>>() {
         });
         return resultList.getData().getList();
    }


    public boolean elementExistInCurrentScene(BaseSceneElementRequest baseRequest) {
        String csceneInstanceId = baseRequest.getSceneId();
        String sceneInstanceId = csceneInstanceMapper.get(csceneInstanceId).getScene();
        var sceneElements = sceneElementMapper.list(new HumanCondition().select(SceneElementModel::getId)
                .filterEqual(SceneElementModel::getScene, sceneInstanceId).filterEqual(SceneElementModel::getNodeId, baseRequest.getElementIds().get(0)));
        if (CollectionUtils.isEmpty(sceneElements)) {
            log.info("当前节点{}不是当前靶场{}节点", baseRequest.getElementIds().get(0), baseRequest.getSceneId());
            return false;
        } else {
            return true;
        }
    }

    public Set<String> elementsExistInCurrentScene(String csceneInstanceId, Set<String> allElements) {
        if (CollectionUtils.isEmpty(allElements)) {
            return Collections.emptySet();
        } else {
            String sceneInstanceId = csceneInstanceMapper.get(csceneInstanceId).getScene();
            var sceneElements = sceneElementMapper.list(new HumanCondition()
                            .select(SceneElementModel::getNodeId)
                    .filterEqual(SceneElementModel::getScene, sceneInstanceId)
                    .filterIn(SceneElementModel::getNodeId, allElements.toArray(new String[]{})));
            if (CollectionUtils.isEmpty(sceneElements)) {
                return Collections.emptySet();
            } else {
                return sceneElements.stream().map(SceneElementModel::getNodeId).collect(Collectors.toSet());
            }
        }

    }

    public Set<String> getAllNodeIdsInTestRangeByCurrentCsceneIds(String csceneInstanceId) {
        var allElementInfo = listAllElementBasicInfo(csceneInstanceId, new SceneAllElementsQueryRequest());
        return allElementInfo.stream().map(element -> element.getId()).collect(Collectors.toSet());
    }

    public Set<String> getAllNodeIdsInCurrentCscene(String csceneInstanceId) {
        String sceneInstanceId = csceneInstanceMapper.get(csceneInstanceId).getScene();
        var sceneElements = sceneElementMapper.list(new HumanCondition().select(SceneElementModel::getNodeId)
                .filterEqual(SceneElementModel::getScene, sceneInstanceId)
                .filterEqual(SceneElementModel::getCurrentScenePartFlag, 1));
        return sceneElements.stream().map(SceneElementModel::getNodeId).collect(Collectors.toSet());
    }




}
