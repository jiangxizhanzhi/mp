package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import cn.cyberpeace.mp.scene.model.SceneInstanceStartHisModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneAreaMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneInstanceStartHisModel.class)
public interface SceneInstanceStartHisMapper extends BaseModelMapper<SceneInstanceStartHisModel> {

}
