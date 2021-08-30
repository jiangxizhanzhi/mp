package cn.cyberpeace.mp.scene.core.scene;

import cn.cyberpeace.mp.base.annotation.BeanFieldClasses;
import cn.cyberpeace.mp.base.annotation.ClassFieldClasses;
import cn.cyberpeace.mp.scene.core.base.BaseSceneHandler;
import cn.cyberpeace.mp.scene.core.bo.*;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginContext;
import cn.cyberpeace.mp.scene.core.plugin.ScenePluginHandler;
import cn.cyberpeace.mp.scene.model.*;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortModel;
import cn.cyberpeace.mp.scene.model.base.BaseSceneNodeModel;
import cn.cyberpeace.mp.scene.repository.*;

/**
 * 场景实例的处理
 * 场景实例的功能由本类对外提供
 */
@ClassFieldClasses({
        SceneDataBO.class,
        SceneTopologyBO.class,
        SceneBO.class,
        SceneAreaBO.class,
        SceneElementBO.class,
        SceneElementPortBO.class,
        SceneElementPortConnectionBO.class,
        SceneModel.class,
        SceneAreaModel.class,
        SceneElementModel.class,
        SceneElementPortModel.class,
        SceneElementPortConnectionModel.class,
        SceneElementPluginModel.class,
        ScenePluginHandler.class,
        SceneHandler.class,
})
@BeanFieldClasses({
        SceneMapper.class,
        SceneAreaMapper.class,
        SceneElementMapper.class,
        SceneElementPortMapper.class,
        SceneElementPortConnectionMapper.class,
        SceneElementPluginMapper.class,
})
public class SceneHandler extends BaseSceneHandler<
        SceneModel,
        SceneAreaModel,
        SceneElementModel,
        SceneElementPortModel,
        SceneElementPortConnectionModel,
        SceneElementPluginModel,
        SceneBO,
        SceneAreaBO,
        SceneElementPortBO,
        SceneElementBO,
        SceneElementPortConnectionBO,
        SceneTopologyBO,
        SceneDataBO,
        SceneMapper,
        SceneAreaMapper,
        SceneElementMapper,
        SceneElementPortMapper,
        SceneElementPortConnectionMapper,
        SceneElementPluginMapper,
        ScenePluginHandler<?, ?, ?>,
        SceneHandler> {

    public SceneHandler() {}

    public SceneHandler(SceneDataBO sceneDataBO) {
        init(sceneDataBO, false);
    }

    public SceneHandler(SceneDataBO sceneDataBO, boolean isAreaMode) {
        init(sceneDataBO, isAreaMode);
    }

    public SceneHandler(String sceneId, String areaId) {
        init(sceneId, areaId, false);
    }

    public SceneHandler(String sceneId, String areaId, boolean isAreaMode) {
        init(sceneId, areaId, isAreaMode);
    }

    @Override
    public void initPluginContext(String pluginKey, ScenePluginHandler<?, ?, ?> plugin) {
        var context = new ScenePluginContext();
        context.setSceneHandler(this);
        plugin.setContext(context);
    }

    public void initiate() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.initiate();
        });
    }

    public void create() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.create();
        });
    }

    public void start() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.start();
        });
    }

    public void stop() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.stop();
        });
    }

    public void restart() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.restart();
        });
    }

    public void pause() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.pause();
        });
    }

    public void unpause() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.unpause();
        });
    }

    public void snapshot() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.snapshot();
        });
    }

    public void delete() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.delete();
        });
    }

    /**
     * TODO: 修改场景
     */
    public void modify() {
        getPluginMap().forEach((key, plugin) -> {
            plugin.modify();
        });
    }
}
