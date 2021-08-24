package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstaneRoleBO;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.scene.service.SceneService;

import java.util.List;


/**
 * 综合场景实例处理
 */
@ClassFieldClasses({
        CsceneInstanceModel.class,
        CsceneInstanceRoleModel.class,
        CsceneInstaneRoleBO.class,
        CsceneInstanceBO.class,
})
@BeanFieldClasses({
        CsceneInstanceMapper.class,
        CsceneInstanceRoleMapper.class,
})
public class CsceneInstanceHandler extends BaseCsceneHandler<
        CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        CsceneInstanceBO, CsceneInstanceMapper, CsceneInstanceRoleMapper> {

    private SceneService sceneService;

    @Override
    public void initBase() {
        super.initBase();
        sceneService = BeanUtils.getBean(SceneService.class);
    }

    public CsceneInstanceHandler(CsceneInstanceBO csceneBO) {
        init(csceneBO);
    }

    public CsceneInstanceHandler(String csceneId) {
        init(csceneId);
    }

    public CsceneInstanceHandler(CsceneInstanceModel csceneObj, List<CsceneInstanceRoleModel> roleObjs) {
        init(csceneObj, roleObjs);
    }

    @Override
    public String getSceneId(CsceneInstanceModel csceneObj) {
        return csceneObj.getScene();
    }

    @Override
    public void loadScene(CsceneInstanceBO csceneBO, String sceneId) {
        csceneBO.setScene(sceneService.get(sceneId, false, null).getSource());
    }

    @Override
    public void saveScene(CsceneInstanceBO csceneBO) {
//        csceneBO.setScene(sceneService.create(csceneBO.getScene(), false).getSource());
    }

    @Override
    public void deleteScene(CsceneInstanceBO csceneBO) {
        var sceneData = csceneBO.getScene();
        String sceneId = sceneData == null ? null : sceneData.getTopology().getScene().getEid();
        deleteScene(sceneId);
        csceneBO.setScene(null);
    }

    @Override
    public void deleteScene(String sceneId) {
        if (sceneId != null) {
            sceneService.delete(sceneId, true);
        }
    }
}
