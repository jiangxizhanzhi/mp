package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("mp_scene_scene")
public class SceneModel extends BaseSceneModel {
    private Integer event;
    private Integer eventStatus;
    private Integer process;
    private Integer processStatus;
    private Integer processMessage;
}
