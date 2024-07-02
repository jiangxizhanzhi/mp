package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaNodeConnectionModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName SceneConfigAreaNodeConnectionModel
 * @Description 场景模板节点连接模型
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaNodeConnectionModel
 */
@Data
@AllArgsConstructor
@Table("mp_scene_scene_config_node_connection")
public class SceneConfigAreaNodeConnectionModel extends BaseSceneAreaNodeConnectionModel {
}
