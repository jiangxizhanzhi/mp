package cn.cyberpeace.mp.sceneplugingns3.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugingns3.model.base.BaseGns3PluginSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugingns3_scene")
public class Gns3PluginSceneModel extends BaseGns3PluginSceneModel {
    @DefaultStr
    private String resourceId;

    @DefaultStr
    private String bridgeResourceId;
}
