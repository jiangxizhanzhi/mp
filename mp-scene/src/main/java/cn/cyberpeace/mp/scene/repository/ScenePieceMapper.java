package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.ScenePieceModel;
import cn.cyberpeace.mp.scene.repository.base.BaseScenePieceMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName ScenePieceMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseScenePieceMapper
 */
@Mapper
@Repository
@ModelClass(ScenePieceModel.class)
public interface ScenePieceMapper extends BaseScenePieceMapper<ScenePieceModel> {
}
