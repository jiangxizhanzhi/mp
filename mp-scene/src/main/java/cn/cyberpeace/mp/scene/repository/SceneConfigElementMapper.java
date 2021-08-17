package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigElementModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneConfigElementModel.class)
public interface SceneConfigElementMapper extends BaseSceneElementMapper<SceneConfigElementModel> {
}
