package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaNodeConnectionModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 场景模板节点连接模型
 *
 * @author 何韦
 * @date 2023/05/05
 */
@Data
@AllArgsConstructor
@Table("mp_scene_scene_config_node_connection")
public class SceneConfigAreaNodeConnectionModel extends BaseSceneAreaNodeConnectionModel {
}
