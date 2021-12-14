package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.ScenePieceModel;
import cn.cyberpeace.mp.scene.repository.base.BaseScenePieceMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(ScenePieceModel.class)
public interface ScenePieceMapper extends BaseScenePieceMapper<ScenePieceModel> {
}
