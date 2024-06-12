package cn.cyberpeace.mp.cscene.core.base;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneRoleBO;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneMapper;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneRoleMapper;
import cn.cyberpeace.mp.scene.core.exception.SceneNotExistException;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 综合场景处理
 */
@Getter
public abstract class BaseCsceneHandler<
        TCsceneModel extends BaseCsceneModel,
        TCsceneRoleModel extends BaseCsceneRoleModel,
        TCsceneRoleBO extends BaseCsceneRoleBO<TCsceneRoleModel, TCsceneRoleBO>,
        TCsceneBO extends BaseCsceneBO<TCsceneModel, TCsceneRoleModel, TCsceneRoleBO, TCsceneBO>,
        TCsceneMapper extends BaseCsceneMapper<TCsceneModel>,
        TCsceneRoleMapper extends BaseCsceneRoleMapper<TCsceneRoleModel>> {

    private CsceneContextBO context;

    protected boolean initLazy;

    /** 综合场景id */
    private String csceneId;
    /** 综合场景业务数据 */
    private TCsceneBO cscene;

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    protected Class<TCsceneModel> csceneModelClass;
    protected Class<TCsceneRoleModel> csceneRoleModelClass;
    protected Class<TCsceneBO> csceneBOClass;
    protected Class<TCsceneRoleBO> csceneRoleBOClass;

    // 通过BeanFieldNames注解和实现类BeanFieldClasses注解，初始化调用BeanUtils.loadBeanFields(this)设置
    protected TCsceneMapper csceneMapper;
    protected TCsceneRoleMapper csceneRoleMapper;

    /**
     * 从数据库读取综合场景信息
     * @return 综合场景信息
     */
    protected TCsceneModel readCscene() {
        return csceneMapper.get(csceneId);
    }

    /**
     * 从数据库读取综合场景角色信息
     * @return 综合场景角色信息
     */
    private List<TCsceneRoleModel> readCsceneRoles() {
        return csceneRoleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, csceneId));
    }

    /**
     * 转换综合场景表模型数据为业务模型数据，获取场景配置的结构化数据
     * @param csceneObj 综合场景表模型数据
     * @return 业务模型数据
     */
    private TCsceneBO convertToCsceneBO(TCsceneModel csceneObj) {
        TCsceneBO csceneBO = ClassUtils.createInstance(csceneBOClass);
        csceneBO.fromModel(csceneObj);
        String sceneId = getSceneId(csceneObj);
        if (!StringUtils.isEmpty(sceneId)) {
            loadScene(csceneBO, sceneId);
        }
        return csceneBO;
    }

    /**
     * 转换综合场景/综合场景实例的角色表模型数据为业务模型数据，转换children关系
     * @param roleObjs 综合场景/综合场景实例的角色表模型数据
     * @param roleBOClass 业务模型class
     * @return children关系的角色业务模型数据
     */
    private List<TCsceneRoleBO> convertToRoleBOs(List<TCsceneRoleModel> roleObjs, Class<TCsceneRoleBO> roleBOClass) {
        List<TCsceneRoleBO> roleBOS = new ArrayList<>();
        if (roleObjs == null) {
            return roleBOS;
        }
        Map<String, TCsceneRoleBO> roleBOMap = new HashMap<>();
        Map<String, String> roleParentMap = new HashMap<>();
        for (TCsceneRoleModel roleObj : roleObjs) {
            TCsceneRoleBO roleBO = ClassUtils.createInstance(roleBOClass);
            roleBO.fromModel(roleObj);
            roleBOMap.put(roleObj.getId(), roleBO);
            roleParentMap.put(roleObj.getId(), roleObj.getParent());
            if (StringUtils.isEmpty(roleObj.getParent())) {
                roleBOS.add(roleBO);
            }
        }
        for (Map.Entry<String, TCsceneRoleBO> entry : roleBOMap.entrySet()) {
            String roleId = entry.getKey();
            TCsceneRoleBO roleBO = entry.getValue();
            String parentId = roleParentMap.getOrDefault(roleId, null);
            TCsceneRoleBO parentRoleBO = roleBOMap.getOrDefault(parentId, null);
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
    private List<TCsceneRoleModel> convertToRoleObjs(List<TCsceneRoleBO> roleBOS, String parentId) {
        List<TCsceneRoleModel> roleObjs = new ArrayList<>();
        if (roleBOS == null) {
            return roleObjs;
        }

        for (TCsceneRoleBO roleBO : roleBOS) {
            TCsceneRoleModel roleObj = roleBO.toModel();
            roleObj.setParent(parentId);
            roleObj.initDefault(BaseModel::getId);
            roleObjs.add(roleObj);

            List<TCsceneRoleBO> children = roleBO.getChildren();
            if (children != null) {
                roleObjs.addAll(convertToRoleObjs(children, roleObj.getId()));
            }
        }
        return roleObjs;
    }

    /**
     * 重新载入综合场景数据
     * @return 载入是否成功
     */
    private boolean reloadCscene() {
        cscene = null;
        TCsceneModel csceneObj = readCscene();
        if (csceneObj == null) {
            return false;
        }
        cscene = convertToCsceneBO(csceneObj);
        return true;
    }

    /**
     * 重新载入综合场景角色信息
     * @return 载入是否成功
     */
    private boolean reloadCsceneRoles() {
        if (cscene == null) {
            return false;
        }
        cscene.setRoles(convertToRoleBOs(readCsceneRoles(), csceneRoleBOClass));
        return true;
    }

    /**
     * 保存综合场景数据
     * @return 保存是否成功
     */
    private boolean saveCscene() {
        String sceneId = getSceneId(cscene.toModel());
        if (!StringUtils.isEmpty(csceneId)) {
            TCsceneModel originCsceneObj = readCscene();
            String originSceneId = originCsceneObj == null ? null : getSceneId(originCsceneObj);
            // 场景id变化了删除原来的
            if (!StringUtils.isEmpty(originSceneId) && !originSceneId.equals(sceneId)) {
                deleteScene(originSceneId, true);
            }
        } else {
            UserModel user = UserCommon.getUserWithContext(context);
            if (user != null) {
                cscene.setCreateUser(user.getId());
                cscene.setCreateUsername(user.getUsername());
            }
        }
        saveScene(cscene);

        TCsceneModel csceneObj = cscene.toModel();
        boolean result = csceneMapper.save(csceneObj);
        setCsceneId(csceneObj.getId());
        cscene.fromModel(csceneObj);
        return result;
    }

    /**
     * 保存综合场景角色信息
     */
    private void saveCsceneRoles() {
        List<TCsceneRoleModel> roleObjs = convertToRoleObjs(cscene.getRoles(), null);
        Map<String, TCsceneRoleModel> existRoleObjMap = new HashMap<>();
        for (TCsceneRoleModel x : readCsceneRoles()) {
            if (existRoleObjMap.put(x.getId(), x) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }

        List<TCsceneRoleModel> addingTargets = new ArrayList<>();
        List<TCsceneRoleModel> updatingTargets = new ArrayList<>();
        for (TCsceneRoleModel roleObj : roleObjs) {
            roleObj.setCscene(csceneId);
            String roleId = roleObj.getId();
            if (existRoleObjMap.containsKey(roleId)) {
                updatingTargets.add(roleObj);
                existRoleObjMap.remove(roleId);
            } else {
                addingTargets.add(roleObj);
            }
        }
        String[] removingTargetIds = existRoleObjMap.keySet().toArray(new String[0]);

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

        cscene.setRoles(convertToRoleBOs(ListUtils.union(addingTargets, updatingTargets), csceneRoleBOClass));
    }

    protected void setCsceneId(String csceneId) {
        this.csceneId = csceneId;
    }

    public void initBase() {
        this.initMapper();
        this.initClass();
    }

    protected abstract void initMapper();
    protected abstract void initClass();

    public void initContext(CsceneContextBO context) {
        this.context = context;
        if (context != null) {
            context.loadTransparentContext();
        }
    }

    public void initLazy(String csceneId, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneId);
        this.initLazy = true;
    }

    /**
     * 从完整的综合场景id初始化
     * @param csceneId 综合场景id
     */
    public void init(String csceneId, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneId);
        reload();
        if (this.csceneId != null && this.cscene == null) {
            throw new SceneNotExistException("csceneId:" + csceneId);
        }
    }

    /**
     * 从完整的综合场景业务数据初始化
     * @param TCsceneBO 完整的综合场景业务数据
     */
    public void init(TCsceneBO TCsceneBO, CsceneContextBO context) {
        initBase();
        initContext(context);
        fromData(TCsceneBO);
    }

    /**
     * 从外部查询的综合场景数据初始化
     * @param csceneObj 外部查询的综合场景数据
     * @param roleObjs 外部查询的综合场景角色数据
     */
    public void init(TCsceneModel csceneObj, List<TCsceneRoleModel> roleObjs, CsceneContextBO context) {
        initBase();
        initContext(context);
        TCsceneBO csceneBO = convertToCsceneBO(csceneObj);
        csceneBO.setRoles(convertToRoleBOs(roleObjs, csceneRoleBOClass));
        fromData(csceneBO);
    }

    /**
     * 从完整的综合场景业务数据加载
     * @param TCsceneBO 完整的综合场景业务数据
     */
    public void fromData(TCsceneBO TCsceneBO) {
        setCsceneId(TCsceneBO.getId());
        cscene = TCsceneBO;
    }

    /**
     * 返回完整的综合场景业务数据
     * @return 完整的综合场景业务数据
     */
    public TCsceneBO toData() {
        return cscene;
    }

    /**
     * 重新载入综合场景业务数据
     */
    public void reload() {
        if (!reloadCscene()) {
            return;
        }
        reloadCsceneRoles();
    }

    /**
     * 保存综合场景业务数据
     */
    public void save() {
        saveCscene();
        try {
            saveCsceneRoles();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 删除综合场景业务数据
     */
    public boolean delete(boolean logic) {
        deleteScene(cscene, logic);
        if (logic) {
            return csceneMapper.logicDelete(cscene.getId());
        } else {
            csceneRoleMapper.filterDelete(new HumanCondition()
                    .filterEqual(BaseCsceneRoleModel::getCscene, cscene.getId()));
            return csceneMapper.delete(cscene.getId());
        }
    }

    /**
     * 获取底层场景id
     * @param csceneObj 综合场景
     * @return 底层场景id
     */
    public abstract String getSceneId(TCsceneModel csceneObj);

    /**
     * 载入底层场景数据到综合场景数据
     * @param TCsceneBO 综合场景数据
     * @param sceneId 底层场景id
     */
    public abstract void loadScene(TCsceneBO TCsceneBO, String sceneId);

    /**
     * 保存底层场景
     * @param TCsceneBO 综合场景数据
     */
    public abstract void saveScene(TCsceneBO TCsceneBO);

    /**
     * 删除底层场景
     * @param TCsceneBO 综合场景数据
     */
    public abstract void deleteScene(TCsceneBO TCsceneBO, boolean logic);

    /**
     * 删除底层场景
     * @param sceneId 底层场景id
     */
    public abstract void deleteScene(String sceneId, boolean logic);
}
