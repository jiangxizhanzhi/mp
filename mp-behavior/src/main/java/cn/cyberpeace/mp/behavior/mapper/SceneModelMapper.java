package cn.cyberpeace.mp.behavior.mapper;

import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.behavior.model.SceneModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
@ModelClass(SceneModel.class)
public interface SceneModelMapper extends BaseModelMapper<SceneModel> {

    Boolean getInstanceBySceneAndNode(@Param("scene_id") String sceneId, @Param("node_id") String nodeId);

    String getIdBySceneAndNode(@Param("scene_id") String sceneId, @Param("node_id") String nodeId);
}
