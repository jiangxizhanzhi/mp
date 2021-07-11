package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@ClassFieldClasses({
        CsceneModel.class,
        CsceneRoleModel.class,
        CsceneBO.class,
        CsceneRoleBO.class,
})
@BeanFieldClasses({
        CsceneMapper.class,
        CsceneRoleMapper.class,
})
@Slf4j
public class CsceneHandler extends BaseCsceneHandler<
        CsceneModel, CsceneRoleModel, CsceneRoleBO,
        CsceneBO, CsceneMapper, CsceneRoleMapper> {

    private SceneService sceneService;
    private SceneConfigService sceneConfigService;
    private CsceneInstanceMapper csceneInstanceMapper;
    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;

    
    private List<CsceneInstanceModel> readCsceneInstances() {
        return csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getCscene, getCsceneId())
                .filterEqual(CsceneInstanceModel::getDeleted, false));
    }

    
    private List<CsceneInstanceRoleModel> readCsceneInstanceRoles(String[] csceneInstanceIds) {
        if (csceneInstanceIds == null || csceneInstanceIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneInstanceRoleMapper.list(new HumanCondition()
                .filterIn(CsceneInstanceRoleModel::getCscene, csceneInstanceIds));
    }

    
    private boolean reloadCsceneInstances() {
        if (StringUtils.isEmpty(getCsceneId())) {
            return false;
        }
        var csceneInstanceObjMap = readCsceneInstances().stream()
                .collect(Collectors.toMap(BaseModel::getId, x -> x));
        var instanceRoleObjsMap = readCsceneInstanceRoles(csceneInstanceObjMap.keySet()
                .toArray(new String[0])).stream().collect(
                Collectors.groupingBy(CsceneInstanceRoleModel::getCscene));

        List<CsceneInstanceBO> testScenes = new ArrayList<>();
        List<CsceneInstanceBO> appScenes = new ArrayList<>();
        csceneInstanceObjMap.forEach((instanceId, csceneInstanceObj) -> {
            var roleObjs = instanceRoleObjsMap.getOrDefault(instanceId, new ArrayList<>());
            var csceneInstanceHandler = new CsceneInstanceHandler(csceneInstanceObj, roleObjs, getContext());
            var type = csceneInstanceObj.getType();
            if (type != null && type.equals(CsceneInstanceModel.Type.TEST)) {
                testScenes.add(csceneInstanceHandler.toData());
            } else if (type != null && type.equals(CsceneInstanceModel.Type.APP)) {
                appScenes.add(csceneInstanceHandler.toData());
            }
        });
        getCscene().setTestScenes(testScenes);
        getCscene().setAppScenes(appScenes);
        return true;
    }

    private CsceneInstanceBO createInstance(int type) {
        return createInstance(type, getCscene());
    }

    private CsceneInstanceBO createInstance(int type, CsceneBO cscene) {
        if (cscene == null) {
            return null;
        }
        var csceneInstance = cscene.exportToCsceneInstance();
        return createInstance(type, csceneInstance);
    }

    private CsceneInstanceBO createInstance(int type, CsceneInstanceBO csceneInstance) {
        if (csceneInstance == null) {
            return null;
        }
        csceneInstance.setCscene(getCsceneId());
        csceneInstance.setType(type);
        if (type == CsceneInstanceModel.Type.TEST && csceneInstance.getScene() != null) {
            
            CsceneDataModifier.supplyProtocol(csceneInstance, AccessMode.Protocol.CONSOLE);
        } else if (type == CsceneInstanceModel.Type.APP && csceneInstance.getScene() != null) {
            
            CsceneDataModifier.supplyProtocolForCoreRouter(csceneInstance, AccessMode.Protocol.CONSOLE);
        }
        this.checkVlanIds(getCscene());
        var instanceHandler = new CsceneInstanceHandler(csceneInstance, getContext());
        
        NodeLimitUtils.UpdateResult updateResult = NodeLimitUtils.addLimitedNodeCount(instanceHandler);
        if (!updateResult.isSuccess()) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_USING_NODE_LIMIT.format(String.valueOf(updateResult.getLimitNode()),
                    String.valueOf(updateResult.getExistNode()), String.valueOf(updateResult.getCurrentNode())));
        }
        instanceHandler.create();
        var result = instanceHandler.toData();
        if (result.getScene() != null && result.getScene().getTopology() != null && result.getScene().getTopology().getScene() != null) {
            result.getScene().getTopology().getScene().setProgressPercentage(5);
        }
        return result;
    }

    public CsceneHandler(CsceneBO csceneBO, CsceneContextBO context) {
        init(csceneBO, context);
    }

    public CsceneHandler(String csceneId, CsceneContextBO context) {
        init(csceneId, context);
    }

    public CsceneHandler(CsceneModel csceneObj, List<CsceneRoleModel> roleObjs, CsceneContextBO context) {
        init(csceneObj, roleObjs, context);
    }

    @Override
    public void initBase() {
        super.initBase();
        sceneService = BeanCommon.getBean(SceneService.class);
        sceneConfigService = BeanCommon.getBean(SceneConfigService.class);
        csceneInstanceMapper = BeanCommon.getBean(CsceneInstanceMapper.class);
        csceneInstanceRoleMapper = BeanCommon.getBean(CsceneInstanceRoleMapper.class);
    }

    public CsceneBO toDataWithoutCorrectCsceneData(boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
        }
        CsceneBO csceneBO = getCscene();
        return csceneBO;
    }

    public CsceneBO toData(boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
        }
        CsceneBO csceneBO = getCscene();
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    @Override
    public CsceneBO toData() {
        CsceneBO csceneBO = super.toData();
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    public CsceneInstanceBO createTestInstance(boolean unique) {
        return createTestInstance(unique, getCscene());
    }

    public CsceneInstanceBO createTestInstance(boolean unique, CsceneBO cscene) {
        if (cscene == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            var testScenes = getCscene().getTestScenes();
            if (testScenes != null && !testScenes.isEmpty()) {
                return null;
            }
        }
        return createInstance(CsceneInstanceModel.Type.TEST, cscene);
    }

    public CsceneInstanceBO createTestInstance(boolean unique, CsceneInstanceBO csceneInstance) {
        if (csceneInstance == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            var testScenes = getCscene().getTestScenes();
            if (testScenes != null && !testScenes.isEmpty()) {
                return null;
            }
        }
        return createInstance(CsceneInstanceModel.Type.TEST, csceneInstance);
    }

    public CsceneInstanceBO createAppInstance(boolean unique) {
        return createAppInstance(unique, getCscene());
    }

    public CsceneInstanceBO createAppInstance(boolean unique, CsceneBO cscene) {
        if (cscene == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            var appScenes = getCscene().getAppScenes();
            if (appScenes != null && !appScenes.isEmpty()) {
                return null;
            }
        }

        return createInstance(CsceneInstanceModel.Type.APP, cscene);
    }

    public CsceneInstanceBO createAppInstance(boolean unique, CsceneInstanceBO csceneInstanceBO) {
        if (csceneInstanceBO == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            var appScenes = getCscene().getAppScenes();
            if (appScenes != null && !appScenes.isEmpty()) {
                return null;
            }
        }

        return createInstance(CsceneInstanceModel.Type.APP, csceneInstanceBO);
    }

    @Override
    public boolean delete(boolean logic) {
        return delete(logic, true);
    }

    public boolean delete(boolean logic, boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
            for (var csceneInstance : ListUtils.union(getCscene().getTestScenes(), getCscene().getAppScenes())) {
                var instanceHandler = new CsceneInstanceHandler(csceneInstance, getContext());
                instanceHandler.delete(logic);
            }
        }

        return super.delete(logic);
    }

    @Override
    public String getSceneId(CsceneModel csceneObj) {
        return csceneObj.getSceneConfig();
    }

    @Override
    public void loadScene(CsceneBO csceneBO, String sceneId) {
        csceneBO.setSceneConfig(sceneConfigService.get(sceneId, false, null, null).getSource());
    }

    @Override
    public void saveScene(CsceneBO csceneBO) {
        csceneBO.setSceneConfig(sceneConfigService.sync(csceneBO.getSceneConfig(), false, null).getSource());
    }

    @Override
    public void deleteScene(CsceneBO csceneBO, boolean logic) {
        var sceneData = csceneBO.getSceneConfig();
        String sceneId = (sceneData == null
                || sceneData.getTopology() == null
                || sceneData.getTopology().getScene() == null) ? null : sceneData.getTopology().getScene().getEid();
        deleteScene(sceneId, logic);
        csceneBO.setSceneConfig(null);
    }

    @Override
    public void deleteScene(String sceneId, boolean logic) {
        if (sceneId != null) {
            sceneConfigService.delete(sceneId, logic, null);
        }
    }

    private void checkVlanIds(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            var elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && !elements.isEmpty()) {
                CsceneSpecialService csceneSpecialService = BeanCommon.getBean(CsceneSpecialService.class);
                elements.values().forEach(e -> {
                    if (e.get("image") != null && e.get("virtual_type") != null && e.get("virtual_type").equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                        String image = e.get("image").toString();
                        var serviceResult = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{ image });
                        if (serviceResult != null && serviceResult.getSource() != null && !serviceResult.getSource().isEmpty()) {
                            Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = serviceResult.getSource();
                            List<String> sceneNames = new ArrayList<>();
                            String sceneName = StringUtils.EMPTY;
                            if (MapUtils.isNotEmpty(useStateMap)) {
                                useStateMap.forEach((k, v) -> {
                                    var names = v.stream()
                                            .filter(deviceReferCsceneInstanceStat -> StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName()))
                                            .map(deviceReferCsceneInstanceStat -> deviceReferCsceneInstanceStat.getName())
                                            .collect(Collectors.toList());
                                    sceneNames.addAll(names);
                                });
                                if (CollectionUtils.isNotEmpty(sceneNames)) {
                                    List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                                    sceneName = StringUtils.join(names, ",");
                                }
                                throw new MpRuntimeException(CsceneErrorCode.ENTITY_HAS_BEEN_USED.format(JsonCommon.stringify(image), sceneName));
                            }
                        }
                    }
                    if (e.get("vlan_id") != null && Integer.parseInt(e.get("vlan_id").toString()) != 0) {
                        var result = csceneSpecialService.getInstanceUseVlanId((Integer) e.get("vlan_id")).getSource();
                        if (CollectionUtils.isNotEmpty(result)) {
                            throw new MpRuntimeException(CsceneErrorCode.VLANID_HAS_BEEN_USED.format(String.valueOf(e.get("vlan_id")), JsonCommon.stringify(result.get(0).getName())));
                        }
                    }

                    if (e.get("entity_usbs")!= null) {
                        List<String> entityUsbUuids = new ArrayList<>();
                        List<Map<String, Object>> entityUsbMap = (List<Map<String, Object>>)e.get("entity_usbs");
                        for (Map<String, Object> tmp : entityUsbMap) {
                            entityUsbUuids.add(String.valueOf(tmp.get("uuid")));
                        }
                        if (CollectionUtils.isNotEmpty(entityUsbUuids)) {
                            for (String uuid : entityUsbUuids) {
                                ServiceResult<List<DeviceReferCsceneStat>> referStateServiceUsbResult = csceneSpecialService.statEntityUsbUsedCscenes(uuid);
                                List<DeviceReferCsceneStat> referStateUsbList = referStateServiceUsbResult.getSource();
                                if (CollectionUtils.isNotEmpty(referStateUsbList)) {
                                    List<String> sceneUsbNames = referStateUsbList.stream()
                                            .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                                            .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                                            .collect(Collectors.toList());
                                    String sceneUsbName = StringUtils.join(sceneUsbNames, ",");
                                    throw new MpRuntimeException(CsceneErrorCode.USB_USED_SCENE.format(sceneUsbName));
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
