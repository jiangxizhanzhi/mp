package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaNodeConnectionModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneNodeConnectionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName SceneConfigNodeConnectionMapper
 * @Description 场景模板节点连接映射器
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneNodeConnectionMapper
 */
@Mapper
@Repository
@ModelClass(SceneConfigAreaNodeConnectionModel.class)
public interface SceneConfigNodeConnectionMapper
        extends BaseSceneNodeConnectionMapper<SceneConfigAreaNodeConnectionModel> {
}
