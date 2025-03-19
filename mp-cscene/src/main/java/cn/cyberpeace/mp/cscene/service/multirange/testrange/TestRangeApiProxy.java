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
import cn.cyberpeace.mp.scene.core.bo.HfdyServerVpnConfig;
import cn.cyberpeace.mp.scene.model.SceneElementModel;
import cn.cyberpeace.mp.scene.model.SceneModel;
import cn.cyberpeace.mp.scene.repository.RangeMapper;
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
import java.util.function.Function;
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
    @Autowired
    private RangeMapper rangeMapper;

    private static final Map<String, Map<String, String>> CSCENE_MAP_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, Integer> csceneCollectedNodeSizeMap = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> csceneIdToNodeIds = new ConcurrentHashMap<>();

    @Override
    public Map<String, HfdyServerVpnConfig> getIdentity(String currentCsceneId) {
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
                String hfdyServerVpnConfig = sceneModels.get(0).getServerVpnIp();
                if (StringUtils.isEmpty(hfdyServerVpnConfig)) {
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format("场景" + currentSceneId + "没有配置serverVpnIp"));
                } else {
                    List<HfdyServerVpnConfig> hfdyServerVpnConfigs = JsonCommon.snakeParse(hfdyServerVpnConfig, new TypeReference<List<HfdyServerVpnConfig>>(){} );
                    return hfdyServerVpnConfigs.stream().collect(Collectors.toMap(HfdyServerVpnConfig::getRangeName, Function.identity(), (a, b) -> a));
                }
            }
        }
    }

    @Override
    public String getAuthorizationHeader(String rangeName) {
        return "JWT " + testRangeLoginData.getAuthToken(rangeName);
    }

    public Map<String, String> getAuthHeaderMap(String rangeName) {
        String token = getAuthorizationHeader(rangeName);
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization", token);
        return headers;
    }


    @Override
    public Map<String, String> getRangeMapCscene(String currentCsceneId) {
        if (CSCENE_MAP_CACHE.containsKey(currentCsceneId)) {
            return CSCENE_MAP_CACHE.get(currentCsceneId);
        } else {
            Map<String, HfdyServerVpnConfig> identity = getIdentity(currentCsceneId);
            Map<String, String> mappingCsceneIdToRange = new HashMap<>();
            identity.forEach((rangeName, hfdyServerVpnConfig) -> {
                String result = TestRangeClientUtil.get(rangeName, String.format("%s?vpnServerIp=%s", TestRangeApiUrl.MAP_SCENE_ID, hfdyServerVpnConfig.getIp()), null, getAuthHeaderMap(rangeName), 10000)
                        .getResult();
                String mapCsceneId = JSONObject.parseObject(result).getString("data");
                if (StringUtils.isEmpty(mapCsceneId)) {
                    log.warn("{}靶场无法查询到启动了ip为{}的hfdy vpn server实例", rangeName, hfdyServerVpnConfig.getIp() );
                    throw new MpRuntimeException(BaseErrorCode.CUSTOM_ERROR.format(rangeName + "分靶场无法获取到对应拓扑实例"));
                } else {
                    mappingCsceneIdToRange.put(mapCsceneId, rangeName);
                }
            });
            CSCENE_MAP_CACHE.put(currentCsceneId, mappingCsceneIdToRange);
            return CSCENE_MAP_CACHE.get(currentCsceneId);
        }

    }

    @Override
    public List<ElementMonitorBasicInfo> listAllElementBasicInfo(String csceneId, SceneAllElementsQueryRequest request) {

        Map<String,String> rangeToMappingCsceneId = getRangeMapCscene(csceneId);
        List<ElementMonitorBasicInfo> elementInfos = new ArrayList<>();
        rangeToMappingCsceneId.forEach((mapCsceneId, range) -> {
            String result = TestRangeClientUtil.get(range, String.format(TestRangeApiUrl.BASIC_ELEMENT_MONITOR_INFO, mapCsceneId), request, getAuthHeaderMap(range), 10000)
                    .getResult();
            JSONObject jsonObject = JSONObject.parseObject(result);
            List<ElementMonitorBasicInfo> elementMonitorBasicInfoList = jsonObject.getJSONArray("data").toJavaList(ElementMonitorBasicInfo.class);
            if (CollectionUtils.isNotEmpty(elementMonitorBasicInfoList)) {
                Set<String> nodes = elementMonitorBasicInfoList.stream().map(ElementMonitorBasicInfo::getId).collect(Collectors.toSet());
                csceneIdToNodeIds.put(mapCsceneId, nodes);

                elementInfos.addAll(elementMonitorBasicInfoList);
            }
        });

        return elementInfos;
    }

    @Override
    public OS getElementOs(BaseSceneElementRequest baseRequest) {
        Map<String,String> mappingCsceneIdToRange = getRangeMapCscene(baseRequest.getSceneId());

        for (Map.Entry<String, String> entry : mappingCsceneIdToRange.entrySet()) {
            String range = entry.getValue();
            String mapCsceneId = entry.getKey();
            if (csceneIdToNodeIds.containsKey(mapCsceneId)) {
                baseRequest.setSceneId(mapCsceneId);
                String result = TestRangeClientUtil.get(range, TestRangeApiUrl.ELEMENT_OS, baseRequest, getAuthHeaderMap(range), 10000)
                        .getResult();
                JSONObject jsonObject = JSONObject.parseObject(result);
                return jsonObject.getObject("data", OS.class);
            }
        }
        return new OS();
    }

    @Override
    public ServiceResult<List<HostState>> getElementHostStateTrend(HostStateTrendRequest hostStateTrendRequest) {
        Map<String,String> mappingCsceneIdToRange = getRangeMapCscene(hostStateTrendRequest.getSceneId());

        for (Map.Entry<String, String> entry : mappingCsceneIdToRange.entrySet()) {
            String range = entry.getValue();
            String mapCsceneId = entry.getKey();
            if (csceneIdToNodeIds.containsKey(mapCsceneId)) {
                hostStateTrendRequest.setSceneId(mapCsceneId);
                String result = TestRangeClientUtil.get(range, TestRangeApiUrl.ELEMENT_TREND_STATE, hostStateTrendRequest, getAuthHeaderMap(range), 10000).getResult();
                return JsonCommon.snakeParse(result, new TypeReference<ServiceResult<List<HostState>>>() {
                });
            }
        }
        return new ServiceResult<>(Collections.emptyList());
    }

    @Override
    public ServiceResult<List<HostState>> getElementHostState(BaseSceneElementPageRequest pageRequest) {
        Map<String,String> mappingCsceneIdToRange = getRangeMapCscene(pageRequest.getSceneId());

        for (Map.Entry<String, String> entry : mappingCsceneIdToRange.entrySet()) {
            String range = entry.getValue();
            String mapCsceneId = entry.getKey();
            if (csceneIdToNodeIds.containsKey(mapCsceneId)) {
                pageRequest.setSceneId(mapCsceneId);
                String result = TestRangeClientUtil.get(range, TestRangeApiUrl.ELEMENT_STATE, pageRequest, getAuthHeaderMap(range), 10000).getResult();
                return JsonCommon.snakeParse(result, new TypeReference<ServiceResult<List<HostState>>>() {
                });
            }
        }
        return new ServiceResult<>(Collections.emptyList());
    }

    @Override
    public ListObjServiceResult<Netiface> pageNetifaces(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        Map<String,String> mappingCsceneIdToRange = getRangeMapCscene(listPageInfoQueryParams.getSceneIds().get(0));

        for (Map.Entry<String, String> entry : mappingCsceneIdToRange.entrySet()) {
            String range = entry.getValue();
            String mapCsceneId = entry.getKey();
            if (csceneIdToNodeIds.containsKey(mapCsceneId)) {
                listPageInfoQueryParams.setSceneIds(List.of(mapCsceneId));
                String result = TestRangeClientUtil.get(range, TestRangeApiUrl.ELEMENT_NET_INTERFACE, listPageInfoQueryParams, getAuthHeaderMap(range), 10000).getResult();
                return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Netiface>>() {
                });
            }
        }
        return new ListObjServiceResult<Netiface>(new ListObjResult<>(new ArrayList<Netiface>(), 0));

    }

    @Override
    public ListObjServiceResult<Port> pagePorts(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        Map<String,String> mappingCsceneIdToRange = getRangeMapCscene(listPageInfoQueryParams.getSceneIds().get(0));

        for (Map.Entry<String, String> entry : mappingCsceneIdToRange.entrySet()) {
            String range = entry.getValue();
            String mapCsceneId = entry.getKey();
            if (csceneIdToNodeIds.containsKey(mapCsceneId)) {
                listPageInfoQueryParams.setSceneIds(List.of(mapCsceneId));
                String result = TestRangeClientUtil.get(range, TestRangeApiUrl.ELEMENT_PORT, listPageInfoQueryParams, getAuthHeaderMap(range), 10000).getResult();
                return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Port>>() {
                });
            }
        }
        return new ListObjServiceResult<Port>(new ListObjResult<>(new ArrayList<Port>(), 0));

    }

    @Override
    public ListObjServiceResult<Process> pageProcesses(TimeListPageInfoQueryParams listPageInfoQueryParams) {
        Map<String,String> mappingCsceneIdToRange = getRangeMapCscene(listPageInfoQueryParams.getSceneIds().get(0));

        for (Map.Entry<String, String> entry : mappingCsceneIdToRange.entrySet()) {
            String range = entry.getValue();
            String mapCsceneId = entry.getKey();
            if (csceneIdToNodeIds.containsKey(mapCsceneId)) {
                listPageInfoQueryParams.setSceneIds(List.of(mapCsceneId));
                String result = TestRangeClientUtil.get(range, TestRangeApiUrl.ELEMENT_PROCESS, listPageInfoQueryParams, getAuthHeaderMap(range), 10000).getResult();
                return JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<Process>>() {
                });
            }
        }
        return new ListObjServiceResult<Process>(new ListObjResult<>(new ArrayList<Process>(), 0));
    }

    @Override
    public List<FlowAttackDto> pageFlow(String id, FlowQueryParams queryParams) {
         List<String> targetNodeIds = queryParams.getTargetNodeIds();
         if (CollectionUtils.isEmpty(targetNodeIds)) {
             return new ArrayList<>();
         }
         Map<String, String> mappingCsceneIdToRange = getRangeMapCscene(id);
         List<FlowAttackDto> finalResult = new ArrayList<>();
         mappingCsceneIdToRange.forEach((mappingCsceneId, range) -> {
             queryParams.setId(mappingCsceneId);
             queryParams.setPage(1);
             queryParams.setPerPage(10000);

             String result = TestRangeClientUtil.get(range, TestRangeApiUrl.FLOW_ATTACK_PAGE, queryParams, getAuthHeaderMap(range), 10000).getResult();
             var resultBo = JsonCommon.snakeParse(result, new TypeReference<ListObjServiceResult<FlowAttackDto>>() {
             });
             List<FlowAttackDto> resultList = resultBo.getData().getList();
             finalResult.addAll(resultList);
         });

         return finalResult;
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
                .filterEqual(SceneElementModel::getScene, sceneInstanceId));
        return sceneElements.stream().map(SceneElementModel::getNodeId).collect(Collectors.toSet());
    }




}
