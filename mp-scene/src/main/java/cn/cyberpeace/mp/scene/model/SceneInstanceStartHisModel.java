package cn.cyberpeace.mp.scene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scene_instance_start_his")
public class SceneInstanceStartHisModel extends BaseModel {
    @DefaultStr
    private String sourceSceneId;
    @DefaultStr
    private String instanceId;
    @DefaultStr
    private String name;
    @DefaultInt
    private Integer referenceType;
    @DefaultInt
    private Integer event;
    @DefaultInt
    private Integer eventStatus;
    @DefaultStr
    private String processMessage;
}
