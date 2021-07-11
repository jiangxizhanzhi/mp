package cn.cyberpeace.mp.cscene.core.base;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldNames;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldNames;
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
import java.util.stream.Collectors;



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

    protected boolean initLazy;

    
    private String csceneId;
    
    private C cscene;

    
    private Class<C0> csceneModelClass;
    private Class<R0> csceneRoleModelClass;
    private Class<C> csceneBOClass;
    private Class<R> csceneRoleBOClass;

    
    private M1 csceneMapper;
    private M2 csceneRoleMapper;

    
    protected C0 readCscene() {
        return csceneMapper.get(csceneId);
    }

    
    private List<R0> readCsceneRoles() {
        return csceneRoleMapper.list(new HumanCondition().filterEqual(BaseCsceneRoleModel::getCscene, csceneId));
    }

    
    private C convertToCsceneBO(C0 csceneObj) {
        var csceneBO = ClassUtils.createInstance(csceneBOClass);
        csceneBO.fromModel(csceneObj);
        String sceneId = getSceneId(csceneObj);
        if (!StringUtils.isEmpty(sceneId)) {
            loadScene(csceneBO, sceneId);
        }
        return csceneBO;
    }

    
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

    
    private boolean reloadCscene() {
        cscene = null;
        var csceneObj = readCscene();
        if (csceneObj == null) {
            return false;
        }
        cscene = convertToCsceneBO(csceneObj);
        return true;
    }

    
    private boolean reloadCsceneRoles() {
        if (cscene == null) {
            return false;
        }
        cscene.setRoles(convertToRoleBOs(readCsceneRoles(), csceneRoleBOClass));
        return true;
    }

    
    private boolean saveCscene() {
        var sceneId = getSceneId(cscene.toModel());
        if (!StringUtils.isEmpty(csceneId)) {
            var originCsceneObj = readCscene();
            var originSceneId = originCsceneObj == null ? null : getSceneId(originCsceneObj);
            
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
        setCsceneId(csceneObj.getId());
        cscene.fromModel(csceneObj);
        return result;
    }

    
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

        var deleteResult = csceneRoleMapper.bulkLogicDelete(removingTargetIds).getSuccessCount();
        if (deleteResult < removingTargetIds.length) {
            throw new SceneUpdateException("Cscene role diff delete failed");
        }

        var createResult = csceneRoleMapper.bulkCreate(addingTargets).getSuccessCount();
        if (createResult < addingTargets.size()) {
            throw new SceneUpdateException("Cscene role diff create failed");
        }

        var updateResult = csceneRoleMapper.bulkUpdate(updatingTargets).getSuccessCount();
        if (updateResult < updatingTargets.size()) {
            throw new SceneUpdateException("Cscene role diff update failed");
        }

        cscene.setRoles(convertToRoleBOs(ListUtils.union(addingTargets, updatingTargets), csceneRoleBOClass));
    }

    protected void setCsceneId(String csceneId) {
        this.csceneId = csceneId;
    }

    public void initBase() {
        ClassUtils.loadClassFields(this);
        BeanCommon.loadBeanFields(this);
    }

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

    
    public void init(String csceneId, CsceneContextBO context) {
        initBase();
        initContext(context);
        setCsceneId(csceneId);
        reload();
        if (this.csceneId != null && this.cscene == null) {
            throw new SceneNotExistException("csceneId:" + csceneId);
        }
    }

    
    public void init(C csceneBO, CsceneContextBO context) {
        initBase();
        initContext(context);
        fromData(csceneBO);
    }

    
    public void init(C0 csceneObj, List<R0> roleObjs, CsceneContextBO context) {
        initBase();
        initContext(context);
        var csceneBO = convertToCsceneBO(csceneObj);
        csceneBO.setRoles(convertToRoleBOs(roleObjs, csceneRoleBOClass));
        fromData(csceneBO);
    }

    
    public void fromData(C csceneBO) {
        setCsceneId(csceneBO.getId());
        cscene = csceneBO;
    }

    
    public C toData() {
        return cscene;
    }

    
    public void reload() {
        if (!reloadCscene()) {
            return;
        }
        reloadCsceneRoles();
    }

    
    public void save() {
        saveCscene();
        saveCsceneRoles();
    }

    
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

    
    public abstract String getSceneId(C0 csceneObj);

    
    public abstract void loadScene(C csceneBO, String sceneId);

    
    public abstract void saveScene(C csceneBO);

    
    public abstract void deleteScene(C csceneBO, boolean logic);

    
    public abstract void deleteScene(String sceneId, boolean logic);
}
