package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstaneRoleBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.plugin.CloudScenePluginContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 综合场景实例处理
 */
@ClassFieldClasses({
        CsceneInstanceModel.class,
        CsceneInstanceRoleModel.class,
        CsceneInstanceBO.class,
        CsceneInstaneRoleBO.class,
})
@BeanFieldClasses({
        CsceneInstanceMapper.class,
        CsceneInstanceRoleMapper.class,
})
public class CsceneInstanceHandler extends BaseCsceneHandler<
        CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        CsceneInstanceBO, CsceneInstanceMapper, CsceneInstanceRoleMapper> {

    private SceneService sceneService;

    private SceneContextBO generateSceneContext() {
        var context = getContext();
        if (context == null) {
            return null;
        }

        var sceneContext = new SceneContextBO();
        Map<String, Object> plugins = new HashMap<>();
        plugins.put(cn.cyberpeace.mp.sceneplugincloud.constant.PluginConstant.PLUGIN_KEY,
                new CloudScenePluginContext(context.getAccessUserId()));
        sceneContext.setPlugins(plugins);
        return sceneContext;
    }

    public CsceneInstanceHandler(CsceneInstanceBO csceneBO, CsceneContextBO context) {
        init(csceneBO, context);
    }

    public CsceneInstanceHandler(String csceneId, CsceneContextBO context, boolean initLazy) {
        if (initLazy) {
            initLazy(csceneId, context);
        } else {
            init(csceneId, context);
        }
    }

    public CsceneInstanceHandler(String csceneId, CsceneContextBO context) {
        init(csceneId, context);
    }

    public CsceneInstanceHandler(CsceneInstanceModel csceneObj, List<CsceneInstanceRoleModel> roleObjs,
                                 CsceneContextBO context) {
        init(csceneObj, roleObjs, context);
    }

    @Override
    public void initBase() {
        super.initBase();
        sceneService = BeanUtils.getBean(SceneService.class);
    }

    public void create() {
        var cscene = getCscene();
        cscene.setScene(sceneService.create(cscene.getScene(), generateSceneContext()).getSource());
        save();
    }

    @Override
    public String getSceneId(CsceneInstanceModel csceneObj) {
        return csceneObj.getScene();
    }

    @Override
    public void loadScene(CsceneInstanceBO csceneBO, String sceneId) {
        csceneBO.setScene(sceneService.get(sceneId, false, null, generateSceneContext()).getSource());
    }

    @Override
    public void saveScene(CsceneInstanceBO csceneBO) {
    }

    @Override
    public void deleteScene(CsceneInstanceBO csceneBO, boolean logic) {
        var sceneData = csceneBO.getScene();
        String sceneId = (sceneData == null
                || sceneData.getTopology() == null
                || sceneData.getTopology().getScene() == null) ? null : sceneData.getTopology().getScene().getEid();
        deleteScene(sceneId, logic);
        csceneBO.setScene(null);
    }

    @Override
    public void deleteScene(String sceneId, boolean logic) {
        if (sceneId != null) {
            sceneService.delete(sceneId, logic, generateSceneContext());
        }
    }

    public boolean pause() {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.pause(sceneId, generateSceneContext()).getSource();
    }

    public boolean unpause() {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.unpause(sceneId, generateSceneContext()).getSource();
    }

    public boolean pauseElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.pauseElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    public boolean unpauseElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.unpauseElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    public boolean restartElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.restartElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    public boolean recreateElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.recreateElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    private String getSceneId() {
        if (this.initLazy) {
            CsceneInstanceModel instance = readCscene();
            return instance == null ? null : instance.getScene();
        } else {
            return getCscene() == null ? null : getSceneId(getCscene().toModel());
        }
    }
}
