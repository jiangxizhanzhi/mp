package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private SceneConfigService sceneConfigService;
    @Autowired
    private SceneService sceneService;

    @Autowired
    private CsceneInstanceMapper csceneInstanceMapper;
    @Autowired
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
            var csceneInstanceHandler = new CsceneInstanceHandler(csceneInstanceObj, roleObjs);
            if (CsceneInstanceModel.Type.TEST.equals(csceneInstanceObj.getType())) {
                testScenes.add(csceneInstanceHandler.toData());
            } else if (CsceneInstanceModel.Type.APP.equals(csceneInstanceObj.getType())) {
                appScenes.add(csceneInstanceHandler.toData());
            }
        });
        getCscene().setTestScenes(testScenes);
        getCscene().setAppScenes(appScenes);
        return true;
    }

    public CsceneHandler(CsceneBO csceneBO) {
        init(csceneBO);
    }

    public CsceneHandler(String csceneId) {
        init(csceneId);
    }

    public CsceneHandler(CsceneModel csceneObj, List<CsceneRoleModel> roleObjs) {
        init(csceneObj, roleObjs);
    }

    public CsceneBO toData(boolean withInstance) {
        if (withInstance) {
            reloadCsceneInstances();
        }
        return getCscene();
    }

    @Override
    public String getSceneId(CsceneModel csceneObj) {
        return csceneObj.getSceneConfig();
    }

    @Override
    public void loadScene(CsceneBO csceneBO, String sceneId) {
        csceneBO.setSceneConfig(sceneConfigService.get(sceneId, false, null).getSource());
    }

    @Override
    public void saveScene(CsceneBO csceneBO) {
        csceneBO.setSceneConfig(sceneConfigService.sync(csceneBO.getSceneConfig(), false).getSource());
    }

    @Override
    public void deleteScene(CsceneBO csceneBO) {
        var sceneData = csceneBO.getSceneConfig();
        String sceneId = sceneData == null ? null : sceneData.getTopology().getScene().getEid();
        if (sceneId != null) {
            sceneConfigService.delete(csceneBO.getSceneConfig().getTopology().getScene().getEid(), false);
        }
        csceneBO.setSceneConfig(null);
    }
}
