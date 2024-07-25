package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.cscene.bo.*;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceUserCountService;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.NodeLimitUtils;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * 综合场景实例处理
 */
@Slf4j
@Deprecated
public class BcSceneInstanceHandler extends BaseCsceneHandler<
        CsceneInstanceModel, CsceneInstanceRoleModel> {

    private SceneService sceneService;

    private String areaId;

    /** 综合场景业务数据 */
    @Setter
    @Getter
    private CsceneInstanceBO cscene;

    private CsceneInstanceUserCountService csceneInstanceUserCountService;


    private CsceneInstanceMapper csceneMapper;
    private CsceneInstanceRoleMapper csceneRoleMapper;

    private SceneContextBO generateSceneContext() {
        CsceneContextBO context = getContext();
        if (context == null) {
            return null;
        }

        return context.toSceneContext();
    }

    /**
     * 转换综合场景表模型数据为业务模型数据，获取场景配置的结构化数据
     * @param csceneObj 综合场景表模型数据
     * @return 业务模型数据
     */
    public CsceneInstanceBO convertToCsceneBO(CsceneInstanceModel csceneObj) {
        CsceneInstanceBO csceneBO = ClassUtils.createInstance(CsceneInstanceBO.class);
        csceneBO.fromModel(csceneObj);
        String sceneId = getSceneId(csceneObj);
        if (!StringUtils.isEmpty(sceneId)) {
            loadScene(csceneBO, sceneId);
        }
        return csceneBO;
    }

    @Override
    protected CsceneInstanceModel readCscene() {
        return csceneMapper.get(getCsceneId());
    }
    @Override
    public List<CsceneInstanceRoleModel> readCsceneRoles() {
        return csceneRoleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, getCsceneId()));
    }

    @Override
    public boolean saveCsceneModelObj(CsceneInstanceModel csceneObj) {
        return csceneMapper.save(csceneObj);
    }

    @Override
    public void saveAndCheckRole(String[] removingTargetIds,
                                 List<CsceneInstanceRoleModel> addingTargets,
                                 List<CsceneInstanceRoleModel> updatingTargets){
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

    public boolean delete(boolean logic, String areaId) {
        CsceneInstanceBO cscene = getCscene();
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
    protected void setCsceneId(String csceneId) {
        super.setCsceneId(csceneId);
        CsceneContextHolder.setCsceneInstanceId(csceneId);
    }

    public BcSceneInstanceHandler(CsceneInstanceBO csceneBO, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneBO.getId());
        setCscene(csceneBO);
    }

    public BcSceneInstanceHandler(String csceneId, CsceneContextBO context, boolean initLazy) {
        if (initLazy) {
            initLazy(csceneId, context);
        } else {
            init(csceneId, context);
        }
    }

    public BcSceneInstanceHandler(String csceneId, CsceneContextBO context) {
        init(csceneId, context);
    }

    public BcSceneInstanceHandler(String csceneId,String areaId, CsceneContextBO context) {
        this.areaId = areaId;
        init(csceneId, context);
    }

    public BcSceneInstanceHandler(CsceneInstanceModel csceneObj, List<CsceneInstanceRoleModel> roleObjs,
                                  CsceneContextBO context) {
        initBase();
        initContext(context);
        CsceneInstanceBO csceneBO = convertToCsceneBO(csceneObj);
        csceneBO.setRoles(convertToRoleBOs(roleObjs));
        setCsceneId(csceneBO.getId());
        setCscene(csceneBO);
    }

    private void init(String csceneId, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneId);
        reload();
        if (getCsceneId() != null && getCscene() == null) {
            throw new SceneNotExistException("csceneId:" + csceneId);
        }
    }

    @Override
    public boolean saveCscene() {
        CsceneInstanceBO cscene = getCscene();
        String sceneId = getSceneId(cscene.toModel());
        if (!StringUtils.isEmpty(getCsceneId())) {
            CsceneInstanceModel originCsceneObj = readCscene();
            String originSceneId = originCsceneObj == null ? null : getSceneId(originCsceneObj);
            // 场景id变化了删除原来的
            if (!StringUtils.isEmpty(originSceneId) && !originSceneId.equals(sceneId)) {
                if (sceneId != null) {
                    sceneService.delete(sceneId, true, generateSceneContext());
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

        CsceneInstanceModel csceneObj = cscene.toModel();
        boolean result = saveCsceneModelObj(csceneObj);
        setCsceneId(csceneObj.getId());
        cscene.fromModel(csceneObj);
        return result;
    }

    @Override
    public void saveCsceneRoles() {
        List<CsceneInstanceRoleModel> roleObjs = convertToRoleObjs(getCscene().getRoles(), null);
        Map<String, CsceneInstanceRoleModel> existRoleObjMap = new HashMap<>();
        for (CsceneInstanceRoleModel x : readCsceneRoles()) {
            if (existRoleObjMap.put(x.getId(), x) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        List<CsceneInstanceRoleModel> addingTargets = new ArrayList<>();
        List<CsceneInstanceRoleModel> updatingTargets = new ArrayList<>();
        for (CsceneInstanceRoleModel roleObj : roleObjs) {
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
        CsceneInstanceModel csceneObj = readCscene();
        if (csceneObj == null) {
            return false;
        }
        CsceneInstanceBO csceneInstanceBO = convertToCsceneBO(csceneObj);
        setCscene(csceneInstanceBO);
        return true;
    }

    /**
     * 转换综合场景/综合场景实例的角色业务模型数据为表模型数据
     * @param roleBOS 综合场景/综合场景实例的角色业务模型数据
     * @param parentId 父角色id
     * @return children关系的角色业务模型数据
     */
    public List<CsceneInstanceRoleModel> convertToRoleObjs(List<CsceneInstaneRoleBO> roleBOS, String parentId) {
        List<CsceneInstanceRoleModel> roleObjs = new ArrayList<>();
        if (roleBOS == null) {
            return roleObjs;
        }

        for (CsceneInstaneRoleBO roleBO : roleBOS) {
            CsceneInstanceRoleModel roleObj = roleBO.toModel();
            roleObj.setParent(parentId);
            roleObj.initDefault(BaseModel::getId);
            roleObjs.add(roleObj);

            List<CsceneInstaneRoleBO> children = roleBO.getChildren();
            if (children != null) {
                roleObjs.addAll(convertToRoleObjs(children, roleObj.getId()));
            }
        }
        return roleObjs;
    }
    @Override
    public  void reloadCsceneRoles() {
        if (getCscene() == null) {
            return;
        }
        List<CsceneInstaneRoleBO> csceneInstaneRoleBOS = convertToRoleBOs(readCsceneRoles());
        getCscene().setRoles(csceneInstaneRoleBOS);
    }

    public List<CsceneInstaneRoleBO> convertToRoleBOs(List<CsceneInstanceRoleModel> roleObjs) {
        List<CsceneInstaneRoleBO> roleBOS = new ArrayList<>();
        if (roleObjs == null) {
            return roleBOS;
        }
        Map<String, CsceneInstaneRoleBO> roleBOMap = new HashMap<>();
        Map<String, String> roleParentMap = new HashMap<>();
        for (CsceneInstanceRoleModel roleObj : roleObjs) {
            CsceneInstaneRoleBO roleBO = ClassUtils.createInstance(CsceneInstaneRoleBO.class);
            roleBO.fromModel(roleObj);
            roleBOMap.put(roleObj.getId(), roleBO);
            roleParentMap.put(roleObj.getId(), roleObj.getParent());
            if (StringUtils.isEmpty(roleObj.getParent())) {
                roleBOS.add(roleBO);
            }
        }
        for (Map.Entry<String, CsceneInstaneRoleBO> entry : roleBOMap.entrySet()) {
            String roleId = entry.getKey();
            CsceneInstaneRoleBO roleBO = entry.getValue();
            String parentId = roleParentMap.getOrDefault(roleId, null);
            CsceneInstaneRoleBO parentRoleBO = roleBOMap.getOrDefault(parentId, null);
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

    @Override
    public void initBase() {
        super.initBase();
        sceneService = BeanCommon.getBean(SceneService.class);
        csceneInstanceUserCountService = BeanCommon.getBean(CsceneInstanceUserCountService.class);
    }

    public void create() {
        if (Objects.nonNull(getContext())) {
            csceneInstanceUserCountService.increaseCount(getContext().getAccessUserId());
        }
        CsceneInstanceBO cscene = getCscene();
        // 先保存设置id
        save();
        SceneDataBO result = sceneService.create(cscene.getScene(), generateSceneContext(), Boolean.TRUE.equals(getCscene().getSupportPartialStart())).getSource();
        cscene.setScene(result);

        CsceneInstanceModel csceneObj = cscene.toModel();
        csceneMapper.save(csceneObj);
        cscene.fromModel(csceneObj);
    }

    @Override
    public String getSceneId(CsceneInstanceModel csceneObj) {
        return csceneObj.getScene();
    }

    public void loadScene(CsceneInstanceBO csceneBO, String sceneId) {
        csceneBO.setScene(sceneService.get(sceneId, areaId == null ? false : true, areaId, generateSceneContext())
                                  .getSource());
    }

    /**
     * 保存底层场景
     * @param csceneBO 综合场景数据
     */
    public void saveScene(CsceneInstanceBO csceneBO) {
    }

    /**
     * 删除底层场景
     * @param csceneBO 综合场景数据
     */
    public void deleteScene(CsceneInstanceBO csceneBO, boolean logic, String areaId) {
        log.info("=======delete scene getContext(): {}", getContext());
        if (Objects.nonNull(getContext())) {
            csceneInstanceUserCountService.decreaseCount(getContext().getAccessUserId());
        }
        SceneDataBO sceneData = csceneBO.getScene();
        String sceneId = (sceneData == null
                || sceneData.getTopology() == null
                || sceneData.getTopology().getScene() == null) ? null : sceneData.getTopology().getScene().getEid();
        if (sceneId != null) {
            sceneService.delete(sceneId, logic, generateSceneContext());
        }
        csceneBO.setScene(null);
    }

    @Override
    protected void initMapper() {
        csceneMapper = BeanCommon.getBean(CsceneInstanceMapper.class);
        csceneRoleMapper = BeanCommon.getBean(CsceneInstanceRoleMapper.class);
    }

    @Override
    protected void initClass() {
        log.info("nothing to initClass");
    }

    public void update(SceneHandler originSceneHandler) {
        CsceneInstanceBO cscene = getCscene();
        String sceneId = getSceneId();
        if (StringUtils.isEmpty(sceneId)) {
            CsceneInstanceModel csceneInstanceModel = csceneMapper.first(new HumanCondition().filterEqual(CsceneInstanceModel::getId, cscene.getId()));
            if (csceneInstanceModel != null) {
                sceneId = csceneInstanceModel.getScene();
            }
        }
        final String sid = sceneId;
        cscene.setScene(sceneService.modify(sid, cscene.getScene(), generateSceneContext(), originSceneHandler, () -> {
            //W:Refresh scene after running
            SceneInstanceLinkCacheUtil.refresh(sid);
            try {
                NodeLimitUtils.getAndRefreshCount();
            } catch (Exception e) {
                log.error("failed to refresh node limit count", e);
            }
        }).getSource());

        CsceneInstanceModel csceneObj = cscene.toModel();
        csceneMapper.save(csceneObj);
        csceneObj.setId(cscene.getId());
        cscene.fromModel(csceneObj);
    }

    public String getSceneId() {
        if (this.initLazy) {
            CsceneInstanceModel instance = readCscene();
            return instance == null ? null : instance.getScene();
        } else {
            return getCscene() == null ? null : getSceneId(getCscene().toModel());
        }
    }

    public CsceneInstanceBO toData() {
        // 深拷贝一下 (createHandler是多线程处理的,CsceneInstanceBO有可能是共用的)
        CsceneInstanceBO cscene = getCscene();
        CsceneInstanceBO csceneInstanceBO = JsonCommon.mapToBean(JsonCommon.beanToMap(cscene), CsceneInstanceBO.class);
        CsceneDataModifier.correctReturnCsceneData(csceneInstanceBO);
        String message = null;
        if (csceneInstanceBO.getScene() != null && csceneInstanceBO.getScene().getTopology() != null
                && csceneInstanceBO.getScene().getTopology().getScene() != null) {
            message = csceneInstanceBO.getScene().getTopology().getScene().getProcessMessage();
        }
        if (message != null && message.length() >= 1) {
            String newMessage = message.substring(0, 1).toUpperCase();
            if (message.length() > 1) {
                newMessage = newMessage + message.substring(1).toLowerCase();
            }
            csceneInstanceBO.getScene().getTopology().getScene().setProcessMessage(newMessage);
        }
        return csceneInstanceBO;
    }

    public CsceneInstanceBO toDataWithHidden() {
        // 深拷贝一下 (createHandler是多线程处理的,CsceneInstanceBO有可能是共用的)
        CsceneInstanceBO cscene = getCscene();
        CsceneInstanceBO csceneInstanceBO = JsonCommon.mapToBean(JsonCommon.beanToMap(cscene), CsceneInstanceBO.class);
        CsceneDataModifier.correctReturnCsceneData(csceneInstanceBO, false);
        String message = null;
        if (csceneInstanceBO.getScene() != null && csceneInstanceBO.getScene().getTopology() != null
                && csceneInstanceBO.getScene().getTopology().getScene() != null) {
            message = csceneInstanceBO.getScene().getTopology().getScene().getProcessMessage();
        }
        if (message != null && message.length() >= 1) {
            String newMessage = message.substring(0, 1).toUpperCase();
            if (message.length() > 1) {
                newMessage = newMessage + message.substring(1).toLowerCase();
            }
            csceneInstanceBO.getScene().getTopology().getScene().setProcessMessage(newMessage);
        }
        return csceneInstanceBO;
    }
}
