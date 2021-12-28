package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincollection_scene_probe_router_port")
public class CollectionPluginSceneProbeRouterPortModel extends BaseModel {
    @DefaultStr
    private String scene;
    @DefaultStr
    private String sceneElement;
    @DefaultStr
    private String networkId;
    @DefaultStr
    private String ip;
}
