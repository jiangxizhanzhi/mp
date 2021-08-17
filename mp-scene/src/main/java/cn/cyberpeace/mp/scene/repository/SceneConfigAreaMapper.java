package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneAreaMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneConfigAreaModel.class)
public interface SceneConfigAreaMapper extends BaseSceneAreaMapper<SceneConfigAreaModel> {
}
