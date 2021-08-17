package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneElementPortConnectionModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPortConnectionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneElementPortConnectionModel.class)
public interface SceneElementPortConnectionMapper
        extends BaseSceneElementPortConnectionMapper<SceneElementPortConnectionModel> {
}
