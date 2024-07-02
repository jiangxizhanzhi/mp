package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPluginModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPluginMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneConfigElementPluginMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPluginMapper
 */
@Mapper
@Repository
@ModelClass(SceneConfigElementPluginModel.class)
public interface SceneConfigElementPluginMapper extends BaseSceneElementPluginMapper<SceneConfigElementPluginModel> {
}
