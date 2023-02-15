package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("mp_scene_instance_start_his")
public class SceneInstanceStartHisModel extends BaseModel {
    private String sourceSceneId;
    private String instanceId;
    private String name;
    private Integer referenceType;
    private Integer event;
    private Integer eventStatus;
    private String processMessage;
}
