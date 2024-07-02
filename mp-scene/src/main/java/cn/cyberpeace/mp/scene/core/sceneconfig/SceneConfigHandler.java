package cn.cyberpeace.mp.scene.core.sceneconfig;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.util.basic.ClassUtils;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.SceneAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigAreaNodeConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigContextBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigDataBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigElementPortConnectionBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigPieceBO;
import cn.cyberpeace.mp.scene.core.bo.SceneConfigTopologyBO;
import cn.cyberpeace.mp.scene.core.plugin.SceneConfigPluginHandler;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaNodeConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPluginModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortConnectionModel;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import cn.cyberpeace.mp.scene.model.SceneConfigModel;
import cn.cyberpeace.mp.scene.model.SceneConfigPieceModel;
import cn.cyberpeace.mp.scene.repository.SceneConfigAreaMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigElementMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigElementPluginMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigElementPortConnectionMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigElementPortMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigNodeConnectionMapper;
import cn.cyberpeace.mp.scene.repository.SceneConfigPieceMapper;

import java.lang.reflect.Field;

/**
 * @ClassName SceneConfigHandler
 * @Description 场景模板的处理
 * 场景模板的功能由本类对外提供
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneHandler
 */
public class SceneConfigHandler extends BaseSceneHandler<
        SceneConfigModel,
        SceneConfigAreaModel,
        SceneConfigElementModel,
        SceneConfigElementPortModel,
        SceneConfigElementPortConnectionModel,
        SceneConfigAreaNodeConnectionModel,
        SceneConfigElementPluginModel,
        SceneConfigPieceModel,
        SceneConfigBO,
        SceneConfigAreaBO,
        SceneConfigElementPortBO,
        SceneConfigElementBO,
        SceneConfigElementPortConnectionBO,
        SceneConfigAreaNodeConnectionBO,
        SceneConfigPieceBO,
        SceneConfigTopologyBO,
        SceneConfigDataBO,
        SceneConfigContextBO,
        SceneConfigPluginHandler<?, ?, ?, ?>,
        SceneConfigHandler> {

    public SceneConfigHandler() {}

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO, SceneConfigContextBO context) {
        init(sceneConfigDataBO, false, context);
    }

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO, boolean isAreaMode, SceneConfigContextBO context) {
        init(sceneConfigDataBO, isAreaMode, context);
    }

    public SceneConfigHandler(String sceneId, String areaId, SceneConfigContextBO context) {
        init(sceneId, areaId, false, context);
    }

    public SceneConfigHandler(String sceneId, String areaId, boolean isAreaMode, SceneConfigContextBO context) {
        init(sceneId, areaId, isAreaMode, context);
    }

    public SceneConfigHandler(String sceneId, String areaId, boolean isAreaMode, boolean lazyMode) {
        if (lazyMode) {
            initLazy(sceneId, areaId, isAreaMode);
        } else {
            init(sceneId, areaId, isAreaMode, null);
        }
    }

    public SceneConfigHandler(SceneConfigDataBO sceneConfigDataBO, boolean partMode) {
        if (partMode) {
            initPart(sceneConfigDataBO, true);
        } else {
            init(sceneConfigDataBO, true, null);
        }
    }

    @Override
    protected void initMapper() {
        super.sceneMapper = BeanCommon.getBean(SceneConfigMapper.class);
        super.sceneAreaMapper = BeanCommon.getBean(SceneConfigAreaMapper.class);
        super.sceneElementMapper = BeanCommon.getBean(SceneConfigElementMapper.class);
        super.sceneElementPortMapper = BeanCommon.getBean(SceneConfigElementPortMapper.class);
        super.sceneElementPortConnectionMapper = BeanCommon.getBean(SceneConfigElementPortConnectionMapper.class);
        super.sceneElementPluginMapper = BeanCommon.getBean(SceneConfigElementPluginMapper.class);
        super.scenePieceMapper = BeanCommon.getBean(SceneConfigPieceMapper.class);
        super.sceneNodeConnectionMapper = BeanCommon.getBean(SceneConfigNodeConnectionMapper.class);
    }

    @Override
    protected void initClass() {
        super.sceneDataBOClass = SceneConfigDataBO.class;
        super.sceneTopologyBOClass = SceneConfigTopologyBO.class;
        super.sceneBOClass = SceneConfigBO.class;
        super.sceneAreaBOClass = SceneConfigAreaBO.class;
        super.sceneElementBOClass = SceneConfigElementBO.class;
        super.sceneElementPortBOClass = SceneConfigElementPortBO.class;
        super.sceneElementPortConnectionBOClass = SceneConfigElementPortConnectionBO.class;
        super.scenePieceBOClass = SceneConfigPieceBO.class;
        super.sceneModelClass = SceneConfigModel.class;
        super.sceneAreaModelClass = SceneConfigAreaModel.class;
        super.sceneElementModelClass = SceneConfigElementModel.class;
        super.sceneElementPortModelClass = SceneConfigElementPortModel.class;
        super.sceneElementPortConnectionModelClass = SceneConfigElementPortConnectionModel.class;
        super.sceneElementPluginModelClass = SceneConfigElementPluginModel.class;
        super.scenePieceModelClass = SceneConfigPieceModel.class;
        super.sceneHandlerClass = SceneConfigHandler.class;
        super.sceneNodeConnectionBOClass = SceneConfigAreaNodeConnectionBO.class;

        Field field = ClassUtils.getField(this.getClass(), "scenePluginHandlerInterface", true, false);
        ClassUtils.setFieldValue(field, this, SceneConfigPluginHandler.class);
    }

    @Override
    public void beforeInitPlugin(SceneConfigPluginHandler<?, ?, ?, ?> plugin) {
        plugin.setSceneHandler(this);
    }

}
