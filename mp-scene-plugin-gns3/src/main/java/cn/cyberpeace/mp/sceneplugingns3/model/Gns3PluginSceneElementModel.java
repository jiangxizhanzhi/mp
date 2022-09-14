package cn.cyberpeace.mp.sceneplugingns3.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugingns3.model.base.BaseGns3PluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugingns3_scene_element")
public class Gns3PluginSceneElementModel extends BaseGns3PluginSceneElementModel {
    @DefaultStr
    private String resourceId;
    @DefaultInt
    private Integer dynamicId;
}
