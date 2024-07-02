package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneAreaNodeConnectionModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneNodeConnectionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneNodeConnectionMapper
 * @Description 场景节点连接映射器
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeConnectionMapper
 */
@Mapper
@Repository
@ModelClass(SceneAreaNodeConnectionModel.class)
public interface SceneNodeConnectionMapper
        extends BaseSceneNodeConnectionMapper<SceneAreaNodeConnectionModel> {
}
