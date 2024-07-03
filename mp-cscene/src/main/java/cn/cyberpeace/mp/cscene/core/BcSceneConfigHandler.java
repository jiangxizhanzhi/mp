package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.behavior.service.CsceneUserBehaviorService;
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
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @ClassName BcSceneConfigHandler
 * @Description 综合场景处理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCsceneHandler
 */
@Slf4j
@Deprecated
public class BcSceneConfigHandler extends BaseCsceneHandler<
        CsceneModel, CsceneRoleModel, CsceneRoleBO,
        CsceneBO, CsceneMapper, CsceneRoleMapper> {

    private SceneService sceneService;
    private SceneConfigService sceneConfigService;
    private CsceneInstanceMapper csceneInstanceMapper;
    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;
    private CsceneUserBehaviorService csceneUserBehaviorService;

    /**
     * 从数据库读取综合场景实例信息
     * @return 综合场景实例信息
     */
    private List<CsceneInstanceModel> readCsceneInstances() {
        return csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getCscene, getCsceneId())
                .filterEqual(CsceneInstanceModel::getDeleted, false));
    }

    /**
     * 从数据库读取综合场景实例的角色信息
     * @param csceneInstanceIds 综合场景实例id列表
     * @return 多个综合场景实例的角色信息
     */
    private List<CsceneInstanceRoleModel> readCsceneInstanceRoles(String[] csceneInstanceIds) {
        if (csceneInstanceIds == null || csceneInstanceIds.length == 0) {
            return new ArrayList<>();
        }
        return csceneInstanceRoleMapper.list(new HumanCondition()
                .filterIn(CsceneInstanceRoleModel::getCscene, csceneInstanceIds));
    }

    /**
     * 重新载入综合场景实例数据
     * @return 载入是否成功
     */
    private boolean reloadCsceneInstances() {
        if (StringUtils.isEmpty(getCsceneId())) {
            return false;
        }
        HashMap<String, CsceneInstanceModel> csceneInstanceObjMap = new HashMap<String,CsceneInstanceModel>();
        for (CsceneInstanceModel x : readCsceneInstances()) {
            if (csceneInstanceObjMap.put(x.getId(), x) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        HashMap<String, List<CsceneInstanceRoleModel>> instanceRoleObjsMap = new HashMap<String, List<CsceneInstanceRoleModel>>();
        for (CsceneInstanceRoleModel csceneInstanceRoleModel : readCsceneInstanceRoles(csceneInstanceObjMap.keySet()
                .toArray(new String[0]))) {
            instanceRoleObjsMap.computeIfAbsent(csceneInstanceRoleModel.getCscene(), k -> new ArrayList<>()).add(csceneInstanceRoleModel);
        }

        List<CsceneInstanceBO> testScenes = new ArrayList<>();
        List<CsceneInstanceBO> appScenes = new ArrayList<>();
        for (Map.Entry<String,CsceneInstanceModel> entry : csceneInstanceObjMap.entrySet()) {
            String instanceId = entry.getKey();
            CsceneInstanceModel csceneInstanceObj = entry.getValue();
            List<CsceneInstanceRoleModel> roleObjs = instanceRoleObjsMap.getOrDefault(instanceId, new ArrayList<>());
            BcSceneInstanceHandler bcSceneInstanceHandler = new BcSceneInstanceHandler(csceneInstanceObj, roleObjs, getContext());
            Integer type = csceneInstanceObj.getType();
            if (type != null && type.equals(CsceneInstanceModel.Type.TEST)) {
                testScenes.add(bcSceneInstanceHandler.toData());
            } else if (type != null && type.equals(CsceneInstanceModel.Type.APP)) {
                appScenes.add(bcSceneInstanceHandler.toData());
            }
        }
        getCscene().setTestScenes(testScenes);
        getCscene().setAppScenes(appScenes);
        return true;
    }

    /**
     * createInstance
     *
     * @param type
     * @return
     */
    private CsceneInstanceBO createInstance(int type) {
        return createInstance(type, getCscene());
    }

    /**
     * createInstance
     *
     * @param type
     * @param cscene
     * @return
     */
    private CsceneInstanceBO createInstance(int type, CsceneBO cscene) {
        if (cscene == null) {
            return null;
        }
        CsceneInstanceBO csceneInstance = cscene.exportToCsceneInstance();
        return createInstance(type, csceneInstance);
    }

    /**
     * createInstance
     *
     * @param type
     * @param csceneInstance
     * @return
     */
    private CsceneInstanceBO createInstance(int type, CsceneInstanceBO csceneInstance) {
        if (csceneInstance == null) {
            return null;
        }
        csceneInstance.setCscene(getCsceneId());
        csceneInstance.setType(type);
        if (type == CsceneInstanceModel.Type.TEST && csceneInstance.getScene() != null) {
            // 测试模式下,需要默认可以有console登录协议
            CsceneDataModifier.supplyProtocol(csceneInstance, AccessMode.Protocol.CONSOLE);
        } else if (type == CsceneInstanceModel.Type.APP && csceneInstance.getScene() != null) {
            // 非测试模式,核心路由器需要加上console登录协议
            CsceneDataModifier.supplyProtocolForCoreRouter(csceneInstance, AccessMode.Protocol.CONSOLE);
        }
        this.checkVlanIds(getCscene());
        BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(csceneInstance, getContext());
        // 节点数量增加失败 说明已到达数量上限
        NodeLimitUtils.UpdateResult updateResult = NodeLimitUtils.addLimitedNodeCount(instanceHandler.getCscene());
        if (!updateResult.isSuccess()) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_USING_NODE_LIMIT.format(String.valueOf(updateResult.getLimitNode()),
                    String.valueOf(updateResult.getExistNode()), String.valueOf(updateResult.getCurrentNode())));
        }
        instanceHandler.create();
        CsceneInstanceBO result = instanceHandler.toData();
        if (result.getScene() != null && result.getScene().getTopology() != null && result.getScene().getTopology().getScene() != null) {
            result.getScene().getTopology().getScene().setProgressPercentage(5);
            csceneUserBehaviorService.create(result.getScene().getTopology().getElements().stream().map(SceneElementBO::getId).collect(Collectors.toList()), csceneInstance.getId());
        }
        return result;
    }

    /**
     * BcSceneConfigHandler
     *
     * @param csceneBO
     * @param context
     */
    public BcSceneConfigHandler(CsceneBO csceneBO, CsceneContextBO context) {
        init(csceneBO, context);
    }

    /**
     * BcSceneConfigHandler
     *
     * @param csceneId
     * @param context
     */
    public BcSceneConfigHandler(String csceneId, CsceneContextBO context) {
        init(csceneId, context);
    }

    /**
     * BcSceneConfigHandler
     *
     * @param csceneObj
     * @param roleObjs
     * @param context
     */
    public BcSceneConfigHandler(CsceneModel csceneObj, List<CsceneRoleModel> roleObjs, CsceneContextBO context) {
        init(csceneObj, roleObjs, context);
    }

    /**
     * initBase
     */
    @Override
    public void initBase() {
        super.initBase();
        sceneService = BeanCommon.getBean(SceneService.class);
        sceneConfigService = BeanCommon.getBean(SceneConfigService.class);
        csceneInstanceMapper = BeanCommon.getBean(CsceneInstanceMapper.class);
        csceneInstanceRoleMapper = BeanCommon.getBean(CsceneInstanceRoleMapper.class);
        csceneUserBehaviorService = BeanCommon.getBean(CsceneUserBehaviorService.class);
    }

    /**
     * toDataWithoutCorrectCsceneData
     *
     * @param withInstance
     * @return
     */
    public CsceneBO toDataWithoutCorrectCsceneData(boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
        }
        CsceneBO csceneBO = getCscene();
        return csceneBO;
    }

    /**
     * toData
     *
     * @param withInstance
     * @return
     */
    public CsceneBO toData(boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
        }
        CsceneBO csceneBO = getCscene();
        csceneBO.calHardware();
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    /**
     * toData
     *
     * @return
     */
    @Override
    public CsceneBO toData() {
        CsceneBO csceneBO = super.toData();
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    /**
     * createTestInstance
     *
     * @param unique
     * @return
     */
    public CsceneInstanceBO createTestInstance(boolean unique) {
        return createTestInstance(unique, getCscene());
    }

    /**
     * createTestInstance
     *
     * @param unique
     * @param cscene
     * @return
     */
    public CsceneInstanceBO createTestInstance(boolean unique, CsceneBO cscene) {
        if (cscene == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            List<CsceneInstanceBO> testScenes = getCscene().getTestScenes();
            if (testScenes != null && !testScenes.isEmpty()) {
                return null;
            }
        }
        return createInstance(CsceneInstanceModel.Type.TEST, cscene);
    }

    /**
     * createTestInstance
     *
     * @param unique
     * @param csceneInstance
     * @return
     */
    public CsceneInstanceBO createTestInstance(boolean unique, CsceneInstanceBO csceneInstance) {
        if (csceneInstance == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            List<CsceneInstanceBO> testScenes = getCscene().getTestScenes();
            if (testScenes != null && !testScenes.isEmpty()) {
                return null;
            }
        }
        return createInstance(CsceneInstanceModel.Type.TEST, csceneInstance);
    }

    /**
     * createAppInstance
     *
     * @param unique
     * @return
     */
    public CsceneInstanceBO createAppInstance(boolean unique) {
        return createAppInstance(unique, getCscene());
    }

    /**
     * createAppInstance
     *
     * @param unique
     * @param cscene
     * @return
     */
    public CsceneInstanceBO createAppInstance(boolean unique, CsceneBO cscene) {
        if (cscene == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            List<CsceneInstanceBO> appScenes = getCscene().getAppScenes();
            if (appScenes != null && !appScenes.isEmpty()) {
                return null;
            }
        }

        return createInstance(CsceneInstanceModel.Type.APP, cscene);
    }

    /**
     * createAppInstance
     *
     * @param unique
     * @param csceneInstanceBO
     * @return
     */
    public CsceneInstanceBO createAppInstance(boolean unique, CsceneInstanceBO csceneInstanceBO) {
        if (csceneInstanceBO == null) {
            return null;
        }

        if (unique) {
            reloadCsceneInstances();
            List<CsceneInstanceBO> appScenes = getCscene().getAppScenes();
            if (appScenes != null && !appScenes.isEmpty()) {
                return null;
            }
        }

        return createInstance(CsceneInstanceModel.Type.APP, csceneInstanceBO);
    }

    /**
     * delete
     *
     * @param logic
     * @return boolean
     */
    @Override
    public boolean delete(boolean logic) {
        return delete(logic, true);
    }

    /**
     * delete
     *
     * @param logic
     * @param withInstance
     * @return boolean
     */
    public boolean delete(boolean logic, boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
            for (CsceneInstanceBO csceneInstance : ListUtils.union(getCscene().getTestScenes(), getCscene().getAppScenes())) {
                BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(csceneInstance, getContext());
                instanceHandler.delete(logic);
            }
        }

        return super.delete(logic);
    }

    /**
     * getSceneId
     *
     * @param csceneObj
     * @return
     */
    @Override
    public String getSceneId(CsceneModel csceneObj) {
        return csceneObj.getSceneConfig();
    }

    /**
     * loadScene
     *
     * @param csceneBO
     * @param sceneId
     */
    @Override
    public void loadScene(CsceneBO csceneBO, String sceneId) {
        csceneBO.setSceneConfig(sceneConfigService.get(sceneId, false, null, null).getSource());
    }

    /**
     * saveScene
     *
     * @param csceneBO
     */
    @Override
    public void saveScene(CsceneBO csceneBO) {
        String areaId = csceneBO.getSceneConfig().getTopology().getAreaId();
        csceneBO.setSceneConfig(sceneConfigService.sync(csceneBO.getSceneConfig(), areaId == null ? false : true, null)
                                        .getSource());
    }

    /**
     * deleteScene
     *
     * @param csceneBO
     * @param logic
     */
    @Override
    public void deleteScene(CsceneBO csceneBO, boolean logic) {
        SceneConfigDataBO sceneData = csceneBO.getSceneConfig();
        String sceneId = (sceneData == null
                || sceneData.getTopology() == null
                || sceneData.getTopology().getScene() == null) ? null : sceneData.getTopology().getScene().getEid();
        deleteScene(sceneId, logic);
        csceneBO.setSceneConfig(null);
    }

    /**
     * deleteScene
     *
     * @param sceneId
     * @param logic
     */
    @Override
    public void deleteScene(String sceneId, boolean logic) {
        if (sceneId != null) {
            sceneConfigService.delete(sceneId, logic, null);
        }
    }

    /**
     * initMapper
     */
    @Override
    protected void initMapper() {
        super.csceneMapper = BeanCommon.getBean(CsceneMapper.class);
        super.csceneRoleMapper = BeanCommon.getBean(CsceneRoleMapper.class);
    }

    /**
     * initClass
     */
    @Override
    protected void initClass() {
        super.csceneModelClass = CsceneModel.class;
        super.csceneRoleModelClass = CsceneRoleModel.class;
        super.csceneBOClass = CsceneBO.class;
        super.csceneRoleBOClass = CsceneRoleBO.class;
    }

    /**
     * checkVlanIds
     *
     * @param csceneBO
     */
    private void checkVlanIds(CsceneBO csceneBO) {
        if (csceneBO == null) {
            return;
        }
        if (csceneBO.getSceneConfig() != null && csceneBO.getSceneConfig().getPlugins() != null && csceneBO.getSceneConfig().getPlugins().get("cpcloud") != null) {
            Map<String, Map<String, Object>> elements = csceneBO.getSceneConfig().getPlugins().get("cpcloud").getElements();
            if (elements != null && !elements.isEmpty()) {
                CsceneSpecialService csceneSpecialService = BeanCommon.getBean(CsceneSpecialService.class);
                for (Map<String, Object> e : elements.values()) {
                    if (e.get("image") != null && e.get("virtual_type") != null && e.get("virtual_type").equals(BaseCloudPluginSceneElementModel.VirtualType.ENTITY_NODE)) {
                        String image = e.get("image").toString();
                        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> serviceResult = csceneSpecialService.statDeviceReferCsceneInstances(new String[]{image});
                        if (serviceResult != null && serviceResult.getSource() != null && !serviceResult.getSource().isEmpty()) {
                            Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = serviceResult.getSource();
                            List<String> sceneNames = new ArrayList<>();
                            String sceneName = StringUtils.EMPTY;
                            if (MapUtils.isNotEmpty(useStateMap)) {
                                for (Map.Entry<String, List<DeviceReferCsceneInstanceStat>> entry : useStateMap.entrySet()) {
                                    String k = entry.getKey();
                                    List<DeviceReferCsceneInstanceStat> v = entry.getValue();
                                    List<String> names = new ArrayList<>();
                                    for (DeviceReferCsceneInstanceStat deviceReferCsceneInstanceStat : v) {
                                        if (StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName())) {
                                            String name = deviceReferCsceneInstanceStat.getName();
                                            names.add(name);
                                        }
                                    }
                                    sceneNames.addAll(names);
                                }
                                if (CollectionUtils.isNotEmpty(sceneNames)) {
                                    List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                                    sceneName = StringUtils.join(names, ",");
                                }
                                throw new MpRuntimeException(CsceneErrorCode.ENTITY_HAS_BEEN_USED.format(JsonCommon.stringify(image), sceneName));
                            }
                        }
                    }
                    if (e.get("vlan_id") != null && Integer.parseInt(e.get("vlan_id").toString()) != 0) {
                        List<CsceneInstanceModel> result = csceneSpecialService.getInstanceUseVlanId((Integer) e.get("vlan_id")).getSource();
                        if (CollectionUtils.isNotEmpty(result)) {
                            throw new MpRuntimeException(CsceneErrorCode.VLANID_HAS_BEEN_USED.format(String.valueOf(e.get("vlan_id")), JsonCommon.stringify(result.get(0).getName())));
                        }
                    }

                    if (e.get("entity_usbs") != null) {
                        List<String> entityUsbUuids = new ArrayList<>();
                        List<Map<String, Object>> entityUsbMap = (List<Map<String, Object>>) e.get("entity_usbs");
                        for (Map<String, Object> tmp : entityUsbMap) {
                            entityUsbUuids.add(String.valueOf(tmp.get("uuid")));
                        }
                        if (CollectionUtils.isNotEmpty(entityUsbUuids)) {
                            for (String uuid : entityUsbUuids) {
                                ServiceResult<List<DeviceReferCsceneStat>> referStateServiceUsbResult = csceneSpecialService.statEntityUsbUsedCscenes(uuid);
                                List<DeviceReferCsceneStat> referStateUsbList = referStateServiceUsbResult.getSource();
                                if (CollectionUtils.isNotEmpty(referStateUsbList)) {
                                    List<String> sceneUsbNames = new ArrayList<>();
                                    for (DeviceReferCsceneStat deviceReferCsceneStat : referStateUsbList) {
                                        if (StringUtils.isNotEmpty(deviceReferCsceneStat.getName())) {
                                            String name = deviceReferCsceneStat.getName();
                                            sceneUsbNames.add(name);
                                        }
                                    }
                                    String sceneUsbName = StringUtils.join(sceneUsbNames, ",");
                                    throw new MpRuntimeException(CsceneErrorCode.USB_USED_SCENE.format(sceneUsbName));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
