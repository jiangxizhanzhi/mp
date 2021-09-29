package cn.cyberpeace.mp.cscene.core.base;

import cn.cyberpeace.mp.base.annotation.BeanFieldNames;
import cn.cyberpeace.mp.base.annotation.ClassFieldNames;
import cn.cyberpeace.mp.base.context.SysContextUtils;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.base.util.ClassUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneBO;
import cn.cyberpeace.mp.cscene.bo.base.BaseCsceneRoleBO;
import cn.cyberpeace.mp.cscene.core.common.UserCommon;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneModel;
import cn.cyberpeace.mp.cscene.model.base.BaseCsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneMapper;
import cn.cyberpeace.mp.cscene.repository.base.BaseCsceneRoleMapper;
import cn.cyberpeace.mp.scene.core.exception.SceneUpdateException;
import lombok.Getter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 综合场景处理
 */
@ClassFieldNames({
        "csceneModelClass",
        "csceneRoleModelClass",
        "csceneBOClass",
        "csceneRoleBOClass",
})
@BeanFieldNames({
        "csceneMapper",
        "csceneRoleMapper",
})
@Getter
public abstract class BaseCsceneHandler<
        C0 extends BaseCsceneModel,
        R0 extends BaseCsceneRoleModel,
        R extends BaseCsceneRoleBO<R0, R>,
        C extends BaseCsceneBO<C0, R0, R, C>,
        M1 extends BaseCsceneMapper<C0>,
        M2 extends BaseCsceneRoleMapper<R0>> {

    private CsceneContextBO context;

    /** 综合场景id */
    private String csceneId;
    /** 综合场景业务数据 */
    private C cscene;

    // 通过ClassFieldNames注解和实现类ClassFieldClasses注解，初始化调用ClassUtils.loadClassFields(this)设置
    private Class<C0> csceneModelClass;
    private Class<R0> csceneRoleModelClass;
    private Class<C> csceneBOClass;
    private Class<R> csceneRoleBOClass;

    // 通过BeanFieldNames注解和实现类BeanFieldClasses注解，初始化调用BeanUtils.loadBeanFields(this)设置
    private M1 csceneMapper;
    private M2 csceneRoleMapper;

    /**
     * 从数据库读取综合场景信息
     * @return 综合场景信息
     */
    private C0 readCscene() {
        return csceneMapper.get(csceneId);
    }

    /**
     * 从数据库读取综合场景角色信息
     * @return 综合场景角色信息
     */
    private List<R0> readCsceneRoles() {
        return csceneRoleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, csceneId));
    }

    /**
     * 转换综合场景表模型数据为业务模型数据，获取场景配置的结构化数据
     * @param csceneObj 综合场景表模型数据
     * @return 业务模型数据
     */
    private C convertToCsceneBO(C0 csceneObj) {
        var csceneBO = ClassUtils.createInstance(csceneBOClass);
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
    private List<R> convertToRoleBOs(List<R0> roleObjs, Class<R> roleBOClass) {
        List<R> roleBOs = new ArrayList<>();
        if (roleObjs == null) {
            return roleBOs;
        }
        Map<String, R> roleBOMap = new HashMap<>();
        Map<String, String> roleParentMap = new HashMap<>();
        roleObjs.forEach(roleObj -> {
            var roleBO = ClassUtils.createInstance(roleBOClass);
            roleBO.fromModel(roleObj);
            roleBOMap.put(roleObj.getId(), roleBO);
            roleParentMap.put(roleObj.getId(), roleObj.getParent());
            if (StringUtils.isEmpty(roleObj.getParent())) {
                roleBOs.add(roleBO);
            }
        });
        roleBOMap.forEach((roleId, roleBO) -> {
            var parentId = roleParentMap.getOrDefault(roleId, null);
            var parentRoleBO = roleBOMap.getOrDefault(parentId, null);
            if (parentRoleBO == null) {
                return;
            }

            if (parentRoleBO.getChildren() == null) {
                parentRoleBO.setChildren(new ArrayList<>());
            }
            parentRoleBO.getChildren().add(roleBO);
        });

        return roleBOs;
    }

    /**
     * 转换综合场景/综合场景实例的角色业务模型数据为表模型数据
     * @param roleBOs 综合场景/综合场景实例的角色业务模型数据
     * @param parentId 父角色id
     * @return children关系的角色业务模型数据
     */
    private List<R0> convertToRoleObjs(List<R> roleBOs, String parentId) {
        List<R0> roleObjs = new ArrayList<>();
        if (roleBOs == null) {
            return roleObjs;
        }

        roleBOs.forEach(roleBO -> {
            var roleObj = roleBO.toModel();
            roleObj.setParent(parentId);
            roleObj.initDefault(BaseModel::getId);
            roleObjs.add(roleObj);

            var children = roleBO.getChildren();
            if (children != null) {
                roleObjs.addAll(convertToRoleObjs(children, roleObj.getId()));
            }
        });
        return roleObjs;
    }

    /**
     * 重新载入综合场景数据
     * @return 载入是否成功
     */
    private boolean reloadCscene() {
        cscene = null;
        var csceneObj = readCscene();
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
        var sceneId = getSceneId(cscene.toModel());
        if (!StringUtils.isEmpty(csceneId)) {
            var originCsceneObj = readCscene();
            var originSceneId = originCsceneObj == null ? null : getSceneId(originCsceneObj);
            // 场景id变化了删除原来的
            if (!StringUtils.isEmpty(originSceneId) && !originSceneId.equals(sceneId)) {
                deleteScene(originSceneId, true);
            }
        } else {
            var user = UserCommon.getUserWithContext(context);
            if (user != null) {
                cscene.setCreateUser(user.getId());
                cscene.setCreateUsername(user.getUsername());
            }
        }
        saveScene(cscene);

        C0 csceneObj = cscene.toModel();
        var result = csceneMapper.save(csceneObj);
        csceneId = csceneObj.getId();
        cscene.fromModel(csceneObj);
        return result;
    }

    /**
     * 保存综合场景角色信息
     */
    private void saveCsceneRoles() {
        List<R0> roleObjs = convertToRoleObjs(cscene.getRoles(), null);
        Map<String, R0> existRoleObjMap = readCsceneRoles().stream()
                .collect(Collectors.toMap(BaseModel::getId, x -> x));

        List<R0> addingTargets = new ArrayList<>();
        List<R0> updatingTargets = new ArrayList<>();
        roleObjs.forEach(roleObj -> {
            roleObj.setCscene(csceneId);
            String roleId = roleObj.getId();
            if (existRoleObjMap.containsKey(roleId)) {
                updatingTargets.add(roleObj);
                existRoleObjMap.remove(roleId);
            } else {
                addingTargets.add(roleObj);
            }
        });
        String[] removingTargetIds = existRoleObjMap.keySet().toArray(new String[0]);

        var deleteResult = csceneRoleMapper.bulkLogicDelete(removingTargetIds);
        if (deleteResult < removingTargetIds.length) {
            throw new SceneUpdateException("Cscene role diff delete failed");
        }

        var createResult = csceneRoleMapper.bulkCreate(addingTargets);
        if (createResult < addingTargets.size()) {
            throw new SceneUpdateException("Cscene role diff create failed");
        }

        var updateResult = csceneRoleMapper.bulkUpdate(updatingTargets);
        if (updateResult < updatingTargets.size()) {
            throw new SceneUpdateException("Cscene role diff update failed");
        }

        cscene.setRoles(convertToRoleBOs(ListUtils.union(addingTargets, updatingTargets), csceneRoleBOClass));
    }

    public void initBase() {
        ClassUtils.loadClassFields(this);
        BeanUtils.loadBeanFields(this);
    }

    public void initContext(CsceneContextBO context) {
        this.context = context;
        if (context != null) {
            SysContextUtils.setPassThroughMap(context.getContext());
        }
    }

    /**
     * 从完整的综合场景id初始化
     * @param csceneId 综合场景id
     */
    public void init(String csceneId, CsceneContextBO context) {
        initBase();
        initContext(context);
        this.csceneId = csceneId;
        reload();
    }

    /**
     * 从完整的综合场景业务数据初始化
     * @param csceneBO 完整的综合场景业务数据
     */
    public void init(C csceneBO, CsceneContextBO context) {
        initBase();
        initContext(context);
        fromData(csceneBO);
    }

    /**
     * 从外部查询的综合场景数据初始化
     * @param csceneObj 外部查询的综合场景数据
     * @param roleObjs 外部查询的综合场景角色数据
     */
    public void init(C0 csceneObj, List<R0> roleObjs, CsceneContextBO context) {
        initBase();
        initContext(context);
        var csceneBO = convertToCsceneBO(csceneObj);
        csceneBO.setRoles(convertToRoleBOs(roleObjs, csceneRoleBOClass));
        fromData(csceneBO);
    }

    /**
     * 从完整的综合场景业务数据加载
     * @param csceneBO 完整的综合场景业务数据
     */
    public void fromData(C csceneBO) {
        csceneId = csceneBO.getId();
        cscene = csceneBO;
    }

    /**
     * 返回完整的综合场景业务数据
     * @return 完整的综合场景业务数据
     */
    public C toData() {
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
        saveCsceneRoles();
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
    public abstract String getSceneId(C0 csceneObj);

    /**
     * 载入底层场景数据到综合场景数据
     * @param csceneBO 综合场景数据
     * @param sceneId 底层场景id
     */
    public abstract void loadScene(C csceneBO, String sceneId);

    /**
     * 保存底层场景
     * @param csceneBO 综合场景数据
     */
    public abstract void saveScene(C csceneBO);

    /**
     * 删除底层场景
     * @param csceneBO 综合场景数据
     */
    public abstract void deleteScene(C csceneBO, boolean logic);

    /**
     * 删除底层场景
     * @param sceneId 底层场景id
     */
    public abstract void deleteScene(String sceneId, boolean logic);
}
