package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneAreaModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneAreaMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneAreaMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaMapper
 */
@Mapper
@Repository
@ModelClass(SceneAreaModel.class)
public interface SceneAreaMapper extends BaseSceneAreaMapper<SceneAreaModel> {
}
