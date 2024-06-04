package cn.cyberpeace.mp.cscene.core;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.cscene.bo.CsceneContextBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstaneRoleBO;
import cn.cyberpeace.mp.cscene.context.CsceneContextHolder;
import cn.cyberpeace.mp.cscene.core.base.BaseCsceneHandler;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceModel;
import cn.cyberpeace.mp.cscene.model.CsceneInstanceRoleModel;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceMapper;
import cn.cyberpeace.mp.cscene.repository.CsceneInstanceRoleMapper;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceUserCountService;
import cn.cyberpeace.mp.cscene.util.CsceneDataModifier;
import cn.cyberpeace.mp.scene.core.bo.SceneContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneDataBO;
import cn.cyberpeace.mp.scene.core.scene.SceneHandler;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.sceneplugincloud.util.SceneInstanceLinkCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;


/**
 * 综合场景实例处理
 */
@Slf4j
@Deprecated
public class BcSceneInstanceHandler extends BaseCsceneHandler<
        CsceneInstanceModel, CsceneInstanceRoleModel, CsceneInstaneRoleBO,
        CsceneInstanceBO, CsceneInstanceMapper, CsceneInstanceRoleMapper> {

    private SceneService sceneService;

    private String areaId;

    private CsceneInstanceUserCountService csceneInstanceUserCountService;

    private SceneContextBO generateSceneContext() {
        CsceneContextBO context = getContext();
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

    public BcSceneInstanceHandler(CsceneInstanceBO csceneBO, CsceneContextBO context) {
        init(csceneBO, context);
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
        CsceneInstanceBO cscene = getCscene();
        // 先保存设置id
        save();
        SceneDataBO result = sceneService.create(cscene.getScene(), generateSceneContext(), Boolean.TRUE.equals(getCscene().getSupportPartialStart())).getSource();
        cscene.setScene(result);

        CsceneInstanceModel csceneObj = cscene.toModel();
        getCsceneMapper().save(csceneObj);
        cscene.fromModel(csceneObj);
    }

    @Override
    public String getSceneId(CsceneInstanceModel csceneObj) {
        return csceneObj.getScene();
    }

    @Override
    public void loadScene(CsceneInstanceBO csceneBO, String sceneId) {
        csceneBO.setScene(sceneService.get(sceneId, areaId == null ? false : true, areaId, generateSceneContext())
                                  .getSource());
    }

    @Override
    public void saveScene(CsceneInstanceBO csceneBO) {
    }

    @Override
    public void deleteScene(CsceneInstanceBO csceneBO, boolean logic, String areaId) {
        log.info("=======delete scene getContext(): {}", getContext());
        if (Objects.nonNull(getContext())) {
            csceneInstanceUserCountService.decreaseCount(getContext().getAccessUserId());
        }
        SceneDataBO sceneData = csceneBO.getScene();
        String sceneId = (sceneData == null
                || sceneData.getTopology() == null
                || sceneData.getTopology().getScene() == null) ? null : sceneData.getTopology().getScene().getEid();
        deleteScene(sceneId, logic, areaId);
        csceneBO.setScene(null);
    }

    @Override
    public void deleteScene(String sceneId, boolean logic, String areaId) {
        if (sceneId != null) {
            sceneService.delete(sceneId, logic, generateSceneContext());
        }
    }

    @Override
    protected void initMapper() {
        super.csceneMapper = BeanCommon.getBean(CsceneInstanceMapper.class);
        super.csceneRoleMapper = BeanCommon.getBean(CsceneInstanceRoleMapper.class);
    }

    @Override
    protected void initClass() {
        super.csceneModelClass = CsceneInstanceModel.class;
        super.csceneRoleModelClass = CsceneInstanceRoleModel.class;
        super.csceneBOClass = CsceneInstanceBO.class;
        super.csceneRoleBOClass = CsceneInstaneRoleBO.class;
    }

    public void update(SceneHandler originSceneHandler) {
        CsceneInstanceBO cscene = getCscene();
        String sceneId = getSceneId();
        if (StringUtils.isEmpty(sceneId)) {
            CsceneInstanceModel csceneInstanceModel = getCsceneMapper().first(new HumanCondition().filterEqual(CsceneInstanceModel::getId, cscene.getId()));
            if (csceneInstanceModel != null) {
                sceneId = csceneInstanceModel.getScene();
            }
        }
        final String sid = sceneId;
        cscene.setScene(sceneService.modify(sid, cscene.getScene(), generateSceneContext(), originSceneHandler, () -> {
            //W:Refresh scene after running
            SceneInstanceLinkCacheUtil.refresh(sid);
        }).getSource());

        CsceneInstanceModel csceneObj = cscene.toModel();
        getCsceneMapper().save(csceneObj);
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

    @Override
    public CsceneInstanceBO toData() {
        // 深拷贝一下 (createHandler是多线程处理的,CsceneInstanceBO有可能是共用的)
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
        // 深拷贝一下 (createHandler是多线程处理的,CsceneInstanceBO有可能是共用的)
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
