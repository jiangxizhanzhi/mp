package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneNodeConnectionModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneNodeConnectionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 场景节点连接映射器
 *
 * @author 何韦
 * @date 2023/05/05
 */
@Mapper
@Repository
@ModelClass(SceneNodeConnectionModel.class)
public interface SceneNodeConnectionMapper
        extends BaseSceneNodeConnectionMapper<SceneNodeConnectionModel> {
}
