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
    /**
     * 场景
     */
    @DefaultStr
    private String scene;
    /**
     * 场景网元
     */
    @DefaultStr
    private String sceneElement;
    /**
     * 名称
     */
    @DefaultStr
    private String name;
    /**
     * 源名称
     */
    @DefaultStr
    private String sourceName;
    /**
     * 描述
     */
    @DefaultStr
    private String description;
    /**
     * 地址
     */
    @DefaultStr
    private String address;
}
