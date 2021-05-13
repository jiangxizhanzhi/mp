package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_scene_element")
public class SceneElementModel extends BaseSceneElementModel {
    private Integer event;
    private Integer eventStatus;
    private Integer process;
    private Integer processStatus;
    private Integer processMessage;
}
