package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneElementPortModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPortMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneElementPortMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortMapper
 */
@Mapper
@Repository
@ModelClass(SceneElementPortModel.class)
public interface SceneElementPortMapper extends BaseSceneElementPortMapper<SceneElementPortModel> {
}
