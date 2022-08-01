package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_scene_element_snapshot")
public class SceneElementSnapshotModel extends BaseModel {
    private String sceneConfig;
    private String scene;
    private String sceneElement;
    @DefaultStr
    private String name;
    @DefaultStr
    private String sourceName;
    @DefaultStr
    private String description;
    private String address;
}
