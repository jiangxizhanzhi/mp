package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneElementPluginModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPluginMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneElementPluginModel.class)
public interface SceneElementPluginMapper extends BaseSceneElementPluginMapper<SceneElementPluginModel> {
}
