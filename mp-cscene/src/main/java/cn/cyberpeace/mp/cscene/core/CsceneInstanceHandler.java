package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.stereotype.BeanFieldClasses;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.stereotype.ClassFieldClasses;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstaneRoleBO;
import cn.cyberpeace.mp.cscene.bo.ElementModifyBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceUserCountService;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.cscene.util.LogFileUtil;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;



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
@Slf4j
public class CsceneInstanceHandler extends BaseCsceneHandler<
        CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        CsceneInstanceBO, CsceneInstanceMapper, CsceneInstanceRoleMapper> {

    private SceneService sceneService;

    private CsceneInstanceUserCountService csceneInstanceUserCountService;

    private SceneContextBO generateSceneContext() {
        var context = getContext();
        if (context == null) {
            return null;
        }

        return context.toSceneContext();
    }

    @Override
    protected void setCsceneId(String csceneId) {
        super.setCsceneId(csceneId);
        CsceneContextHolder.setCsceneInstanceId(csceneId);
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
        sceneService = BeanCommon.getBean(SceneService.class);
        csceneInstanceUserCountService = BeanCommon.getBean(CsceneInstanceUserCountService.class);
    }

    public void create() {
        if (Objects.nonNull(getContext())) {
            csceneInstanceUserCountService.increaseCount(getContext().getAccessUserId());
        }
        var cscene = getCscene();
        
        save();
        cscene.setScene(sceneService.create(cscene.getScene(), generateSceneContext(), Boolean.TRUE.equals(getCscene().getSupportPartialStart()), () -> {
            LogFileUtil.getLogFile(cscene.getId());
        }).getSource());

        var csceneObj = cscene.toModel();
        getCsceneMapper().save(csceneObj);
        cscene.fromModel(csceneObj);
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
        log.info("=======delete scene getContext(): {}", getContext());
        if (Objects.nonNull(getContext())) {
            csceneInstanceUserCountService.decreaseCount(getContext().getAccessUserId());
        }
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

    public void update() {
        var cscene = getCscene();
        String sceneId = getSceneId();
        if (StringUtils.isEmpty(sceneId)) {
            var csceneInstanceModel = getCsceneMapper().first(new HumanCondition().filterEqual(CsceneInstanceModel::getId, cscene.getId()));
            if (csceneInstanceModel != null) {
                sceneId = csceneInstanceModel.getScene();
            }
        }
        final String sid = sceneId;
        cscene.setScene(sceneService.modify(sid, cscene.getScene(), generateSceneContext(), ()->{
            
            SceneInstanceLinkCacheUtil.refresh(sid);
        }).getSource());

        var csceneObj = cscene.toModel();
        getCsceneMapper().save(csceneObj);
        csceneObj.setId(cscene.getId());
        cscene.fromModel(csceneObj);
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

    public boolean suspend() {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.suspend(sceneId, generateSceneContext()).getSource();
    }

    public boolean resume() {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.resume(sceneId, generateSceneContext()).getSource();
    }

    
    public boolean startElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.startElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    
    public boolean stopElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.stopElement(sceneId, nodeId, generateSceneContext()).getSource();
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

    public boolean createElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.createElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    public boolean recreateElement(String nodeId, String image) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.recreateElement(sceneId, nodeId, image, generateSceneContext(), () -> {
            SceneInstanceLinkCacheUtil.refresh(sceneId);
        }).getSource();
    }

    public boolean modifyElement(ElementModifyBO element) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        String nodeId = element.getUpdate().getTopology().getElements().get(0).getId();
        return this.sceneService.modifyElement(sceneId, nodeId, element.getUpdate(), generateSceneContext()).getSource();
    }

    public boolean deleteElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.deleteElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    public boolean suspendElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.suspendElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    public boolean resumeElement(String nodeId) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.resumeElement(sceneId, nodeId, generateSceneContext()).getSource();
    }

    public boolean snapshotElement(String nodeId, String snapshotId, String snapshotName, String description) {
        String sceneId = this.getSceneId();
        if (sceneId == null) {
            throw new MpRuntimeException(CsceneErrorCode.SCENE_NOT_EXIST);
        }
        return this.sceneService.snapshotElementSync(sceneId, nodeId, snapshotId, snapshotName, description, generateSceneContext()).getSource();
    }

    public String getSceneId() {
        if (this.initLazy) {
            CsceneInstanceModel instance = readCscene();
            return instance == null ? null : instance.getScene();
        } else {
            return getCscene() == null ? null : getSceneId(getCscene().toModel());
        }
    }

    @Override
    public CsceneInstanceBO toData() {
        
        CsceneInstanceBO csceneInstanceBO = JsonCommon.mapToBean(JsonCommon.beanToMap(super.toData()), CsceneInstanceBO.class);
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
        
        CsceneInstanceBO csceneInstanceBO = JsonCommon.mapToBean(JsonCommon.beanToMap(super.toData()), CsceneInstanceBO.class);
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
