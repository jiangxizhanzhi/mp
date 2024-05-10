package cn.cyberpeace.mp.scene.repository;

import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.scene.model.SceneConfigAreaNodeConnectionModel;
import cn.cyberpeace.mp.scene.repository.base.BaseSceneNodeConnectionMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 场景模板节点连接映射器
 *
 * @author 何韦
 * @date 2023/05/05
 */
@Mapper
@Repository
@ModelClass(SceneConfigAreaNodeConnectionModel.class)
public interface SceneConfigNodeConnectionMapper
        extends BaseSceneNodeConnectionMapper<SceneConfigAreaNodeConnectionModel> {
}
