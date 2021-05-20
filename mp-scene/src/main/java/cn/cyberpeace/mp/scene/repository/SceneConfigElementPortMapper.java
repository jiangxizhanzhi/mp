package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPortMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneConfigElementPortModel.class)
public interface SceneConfigElementPortMapper extends BaseSceneElementPortMapper<SceneConfigElementPortModel> {
}
