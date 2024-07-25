package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.HardwareLimitUtils;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.base.AccessMode;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 综合场景处理
 */
@Slf4j
@Deprecated
public class BcSceneConfigHandler extends BaseCsceneHandler<
        CsceneModel, CsceneRoleModel> {

    private SceneConfigService sceneConfigService;
    private CsceneInstanceMapper csceneInstanceMapper;
    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;

    private CsceneMapper csceneMapper;
    private CsceneRoleMapper csceneRoleMapper;

    /** 综合场景业务数据 */
    @Setter
    @Getter
    private CsceneBO cscene;

    /**
     * 从数据库读取综合场景实例信息
     * @return 综合场景实例信息
     */
    private List<CsceneInstanceModel> readCsceneInstances() {
        return csceneInstanceMapper.list(new HumanCondition().filterEqual(CsceneInstanceModel::getCscene, getCsceneId())
                .filterEqual(CsceneInstanceModel::getDeleted, false));
    }

    /**
     * 转换综合场景表模型数据为业务模型数据，获取场景配置的结构化数据
     * @param csceneObj 综合场景表模型数据
     * @return 业务模型数据
     */
    public CsceneBO convertToCsceneBO(CsceneModel csceneObj) {
        CsceneBO csceneBO = ClassUtils.createInstance(CsceneBO.class);
        csceneBO.fromModel(csceneObj);
        String sceneId = getSceneId(csceneObj);
        if (!StringUtils.isEmpty(sceneId)) {
            loadScene(csceneBO, sceneId);
        }
        return csceneBO;
    }

    @Override
    public boolean saveCsceneModelObj(CsceneModel csceneObj) {
        return csceneMapper.save(csceneObj);
    }

    @Override
    protected CsceneModel readCscene() {
        return csceneMapper.get(getCsceneId());
    }

    @Override
    public List<CsceneRoleModel> readCsceneRoles() {
        return csceneRoleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, getCsceneId()));
    }

    @Override
    public void saveAndCheckRole(String[] removingTargetIds,
                                          List<CsceneRoleModel> addingTargets,
                                          List<CsceneRoleModel> updatingTargets){
        int deleteResult = csceneRoleMapper.bulkLogicDelete(removingTargetIds).getSuccessCount();
        if (deleteResult < removingTargetIds.length) {
            throw new SceneUpdateException("Cscene role diff delete failed");
        }

        int createResult = csceneRoleMapper.bulkCreate(addingTargets).getSuccessCount();
        if (createResult < addingTargets.size()) {
            throw new SceneUpdateException("Cscene role diff create failed");
        }

        int updateResult = csceneRoleMapper.bulkUpdate(updatingTargets).getSuccessCount();
        if (updateResult < updatingTargets.size()) {
            throw new SceneUpdateException("Cscene role diff update failed");
        }

        getCscene().setRoles(convertToRoleBOs(ListUtils.union(addingTargets, updatingTargets)));
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

    private CsceneInstanceBO createInstance(int type) {
        return createInstance(type, getCscene());
    }

    private CsceneInstanceBO createInstance(int type, CsceneBO cscene) {
        if (cscene == null) {
            return null;
        }
        CsceneInstanceBO csceneInstance = cscene.exportToCsceneInstance();
        return createInstance(type, csceneInstance);
    }

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
        HardwareLimitUtils.check(instanceHandler.getCscene());
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
        }
        return result;
    }

    public BcSceneConfigHandler(CsceneBO csceneBO, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneBO.getId());
        setCscene(csceneBO);
    }

    public BcSceneConfigHandler(String csceneId, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneId);
        reload();
        if (getCsceneId() != null && getCscene() == null) {
            throw new SceneNotExistException("csceneId:" + csceneId);
        }
    }

    public BcSceneConfigHandler(CsceneModel csceneObj, List<CsceneRoleModel> roleObjs, CsceneContextBO context) {
        initBase();
        initContext(context);
        CsceneBO csceneBO = convertToCsceneBO(csceneObj);

        csceneBO.setRoles(convertToRoleBOs(roleObjs));

        setCsceneId(csceneBO.getId());
        setCscene(csceneBO);
    }

    @Override
    public void reloadCsceneRoles() {
        if (getCscene() == null) {
            return;
        }
        List<CsceneRoleBO> csceneRoleBOS = convertToRoleBOs(readCsceneRoles());
        getCscene().setRoles(csceneRoleBOS);
    }

    public List<CsceneRoleBO> convertToRoleBOs(List<CsceneRoleModel> roleObjs) {
        List<CsceneRoleBO> roleBOS = new ArrayList<>();
        if (roleObjs == null) {
            return roleBOS;
        }
        Map<String, CsceneRoleBO> roleBOMap = new HashMap<>();
        Map<String, String> roleParentMap = new HashMap<>();
        for (CsceneRoleModel roleObj : roleObjs) {
            CsceneRoleBO roleBO = ClassUtils.createInstance(CsceneRoleBO.class);
            roleBO.fromModel(roleObj);
            roleBOMap.put(roleObj.getId(), roleBO);
            roleParentMap.put(roleObj.getId(), roleObj.getParent());
            if (StringUtils.isEmpty(roleObj.getParent())) {
                roleBOS.add(roleBO);
            }
        }
        for (Map.Entry<String, CsceneRoleBO> entry : roleBOMap.entrySet()) {
            String roleId = entry.getKey();
            CsceneRoleBO roleBO = entry.getValue();
            String parentId = roleParentMap.getOrDefault(roleId, null);
            CsceneRoleBO parentRoleBO = roleBOMap.getOrDefault(parentId, null);
            if (parentRoleBO == null) {
                continue;
            }

            if (parentRoleBO.getChildren() == null) {
                parentRoleBO.setChildren(new ArrayList<>());
            }
            parentRoleBO.getChildren().add(roleBO);
        }

        return roleBOS;
    }

    /**
     * 转换综合场景/综合场景实例的角色业务模型数据为表模型数据
     * @param roleBOS 综合场景/综合场景实例的角色业务模型数据
     * @param parentId 父角色id
     * @return children关系的角色业务模型数据
     */
    public List<CsceneRoleModel> convertToRoleObjs(List<CsceneRoleBO> roleBOS, String parentId) {
        List<CsceneRoleModel> roleObjs = new ArrayList<>();
        if (roleBOS == null) {
            return roleObjs;
        }

        for (CsceneRoleBO roleBO : roleBOS) {
            CsceneRoleModel roleObj = roleBO.toModel();
            roleObj.setParent(parentId);
            roleObj.initDefault(BaseModel::getId);
            roleObjs.add(roleObj);

            List<CsceneRoleBO> children = roleBO.getChildren();
            if (children != null) {
                roleObjs.addAll(convertToRoleObjs(children, roleObj.getId()));
            }
        }
        return roleObjs;
    }


    @Override
    public void initBase() {
        super.initBase();
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
        csceneBO.calHardware();
        CsceneDataModifier.correctReturnCsceneData(csceneBO);
        return csceneBO;
    }

    public CsceneBO toData() {
        CsceneBO csceneBO = getCscene();
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
            List<CsceneInstanceBO> testScenes = getCscene().getTestScenes();
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
            List<CsceneInstanceBO> testScenes = getCscene().getTestScenes();
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
            List<CsceneInstanceBO> appScenes = getCscene().getAppScenes();
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
            List<CsceneInstanceBO> appScenes = getCscene().getAppScenes();
            if (appScenes != null && !appScenes.isEmpty()) {
                return null;
            }
        }

        return createInstance(CsceneInstanceModel.Type.APP, csceneInstanceBO);
    }

    public boolean delete(boolean logic, String areaId) {
        return delete(logic, true, areaId);
    }

    public boolean delete(boolean logic, boolean withInstance, String areaId) {
        if (withInstance) {
            reloadCsceneInstances();
            for (CsceneInstanceBO csceneInstance : ListUtils.union(getCscene().getTestScenes(), getCscene().getAppScenes())) {
                BcSceneInstanceHandler instanceHandler = new BcSceneInstanceHandler(csceneInstance, getContext());
                instanceHandler.delete(logic, null);
            }
        }

        CsceneBO cscene = getCscene();
        deleteScene(cscene, logic, areaId);
        if (logic) {
            return csceneMapper.logicDelete(cscene.getId());
        } else {
            csceneRoleMapper.filterDelete(new HumanCondition()
                    .filterEqual(BaseCsceneRoleModel::getCscene, cscene.getId()));
            return csceneMapper.delete(cscene.getId());
        }
    }

    @Override
    public String getSceneId(CsceneModel csceneObj) {
        return csceneObj.getSceneConfig();
    }

    public void loadScene(CsceneBO csceneBO, String sceneId) {
        csceneBO.setSceneConfig(sceneConfigService.get(sceneId, false, null, null).getSource());
    }

    /**
     * 保存底层场景
     * @param csceneBO 综合场景数据
     */
    public void saveScene(CsceneBO csceneBO) {
        String areaId = csceneBO.getSceneConfig().getTopology().getAreaId();
        csceneBO.setSceneConfig(sceneConfigService.sync(csceneBO.getSceneConfig(), areaId == null ? false : true, null)
                                        .getSource());
    }

    @Override
    protected boolean saveCscene() {
        CsceneBO cscene = getCscene();
        String sceneId = getSceneId(cscene.toModel());
        if (!StringUtils.isEmpty(getCsceneId())) {
            CsceneModel originCsceneObj = readCscene();
            String originSceneId = originCsceneObj == null ? null : getSceneId(originCsceneObj);
            // 场景id变化了删除原来的
            if (!StringUtils.isEmpty(originSceneId) && !originSceneId.equals(sceneId)) {
                if (sceneId != null) {
                    sceneConfigService.delete(sceneId, null, true, null);
                }
            }
        } else {
            UserModel user = UserCommon.getUserWithContext(getContext());
            if (user != null) {
                cscene.setCreateUser(user.getId());
                cscene.setCreateUsername(user.getUsername());
            }
        }
        saveScene(cscene);

        CsceneModel csceneObj = cscene.toModel();
        boolean result = saveCsceneModelObj(csceneObj);
        setCsceneId(csceneObj.getId());
        cscene.fromModel(csceneObj);
        return result;
    }

    @Override
    public void saveCsceneRoles() {
        List<CsceneRoleModel> roleObjs = convertToRoleObjs(getCscene().getRoles(), null);
        Map<String, CsceneRoleModel> existRoleObjMap = new HashMap<>();
        for (CsceneRoleModel x : readCsceneRoles()) {
            if (existRoleObjMap.put(x.getId(), x) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        List<CsceneRoleModel> addingTargets = new ArrayList<>();
        List<CsceneRoleModel> updatingTargets = new ArrayList<>();
        for (CsceneRoleModel roleObj : roleObjs) {
            roleObj.setCscene(getCsceneId());
            String roleId = roleObj.getId();
            if (existRoleObjMap.containsKey(roleId)) {
                updatingTargets.add(roleObj);
                existRoleObjMap.remove(roleId);
            } else {
                addingTargets.add(roleObj);
            }
        }
        String[] removingTargetIds = existRoleObjMap.keySet().toArray(new String[0]);

        saveAndCheckRole(removingTargetIds, addingTargets, updatingTargets);
    }

    @Override
    public boolean reloadCscene() {
        setCscene(null);
        CsceneModel csceneObj = readCscene();
        if (csceneObj == null) {
            return false;
        }
        CsceneBO csceneBO = convertToCsceneBO(csceneObj);
        setCscene(csceneBO);
        return true;
    }

    /**
     * 删除底层场景
     * @param csceneBO 综合场景数据
     */
    public void deleteScene(CsceneBO csceneBO, boolean logic, String areaId) {
        SceneConfigDataBO sceneData = csceneBO.getSceneConfig();
        String sceneId = (sceneData == null
                || sceneData.getTopology() == null
                || sceneData.getTopology().getScene() == null) ? null : sceneData.getTopology().getScene().getEid();
        if (sceneId != null) {
            sceneConfigService.delete(sceneId, areaId, logic, null);
        }
        csceneBO.setSceneConfig(null);
    }

    @Override
    protected void initMapper() {
        csceneMapper = BeanCommon.getBean(CsceneMapper.class);
        csceneRoleMapper = BeanCommon.getBean(CsceneRoleMapper.class);
    }

    @Override
    protected void initClass() {
        log.info("nothing to initClass");
    }

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
