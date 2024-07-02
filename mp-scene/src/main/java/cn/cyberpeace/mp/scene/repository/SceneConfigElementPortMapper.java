package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigElementPortModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneElementPortMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneConfigElementPortMapper
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneElementPortMapper
 */
@Mapper
@Repository
@ModelClass(SceneConfigElementPortModel.class)
public interface SceneConfigElementPortMapper extends BaseSceneElementPortMapper<SceneConfigElementPortModel> {
}
