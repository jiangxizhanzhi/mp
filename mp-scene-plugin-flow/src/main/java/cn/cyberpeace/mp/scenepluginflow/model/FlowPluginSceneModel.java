package cn.cyberpeace.mp.scenepluginflow.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scenepluginflow.model.base.BaseFlowPluginSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scenepluginflow_scene")
public class FlowPluginSceneModel extends BaseFlowPluginSceneModel {
    @DefaultStr
    private String executor;
}
