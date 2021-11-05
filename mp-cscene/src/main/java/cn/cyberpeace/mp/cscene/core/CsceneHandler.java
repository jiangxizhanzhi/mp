package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.util.basic.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneRoleBO;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.model.CsceneModel;
import cn.cyberpeace.mp.cscene.model.CsceneRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneRoleMapper;
import cn.cyberpeace.mp.scene.service.SceneConfigService;
import cn.cyberpeace.mp.scene.service.SceneService;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 综合场景处理
 */
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
public class CsceneHandler extends BaseCsceneHandler<
        CsceneModel, CsceneRoleModel, CsceneRoleBO,
        CsceneBO, CsceneMapper, CsceneRoleMapper> {

    private SceneService sceneService;
    private SceneConfigService sceneConfigService;
    private CsceneInstanceMapper csceneInstanceMapper;
    private CsceneInstanceRoleMapper csceneInstanceRoleMapper;

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
        var instanceHandler = new CsceneInstanceHandler(csceneInstance, getContext());
        instanceHandler.create();
        return instanceHandler.toData();
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
        sceneService = BeanUtils.getBean(SceneService.class);
        sceneConfigService = BeanUtils.getBean(SceneConfigService.class);
        csceneInstanceMapper = BeanUtils.getBean(CsceneInstanceMapper.class);
        csceneInstanceRoleMapper = BeanUtils.getBean(CsceneInstanceRoleMapper.class);
    }

    public CsceneBO toData(boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
        }
        return getCscene();
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
            for (var csceneInstance: ListUtils.union(getCscene().getTestScenes(), getCscene().getAppScenes())) {
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
}
