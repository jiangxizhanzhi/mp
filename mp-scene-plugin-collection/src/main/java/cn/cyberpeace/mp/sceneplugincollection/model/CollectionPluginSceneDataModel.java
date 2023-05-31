package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_sceneplugincollection_scene_data")
public class CollectionPluginSceneDataModel extends BasePluginSceneElementModel {
    @DefaultStr
    private String data;
    @DefaultInt
    private Integer type;

    public interface Type {
        int PORT = 1;
        int NETIFACE = 2;
        int LOGFile = 3;
    }
}
