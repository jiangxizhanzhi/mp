package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_sceneplugincollection_scene_config_element")
public class CollectionPluginSceneConfigElementModel extends BaseCollectionPluginSceneElementModel {
    @DefaultBool
    private Boolean edited;
}
