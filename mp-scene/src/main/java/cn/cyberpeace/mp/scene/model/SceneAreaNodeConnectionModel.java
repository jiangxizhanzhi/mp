package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneAreaNodeConnectionModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName SceneAreaNodeConnectionModel
 * @Description 场景节点连接模型
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseSceneAreaNodeConnectionModel
 */
@Data
@AllArgsConstructor
@Table("mp_scene_scene_node_connection")
public class SceneAreaNodeConnectionModel extends BaseSceneAreaNodeConnectionModel {
}