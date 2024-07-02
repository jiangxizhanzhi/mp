package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectionPluginSceneConfigElementModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneElementModel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_sceneplugincollection_scene_config_element")
public class CollectionPluginSceneConfigElementModel extends BaseCollectionPluginSceneElementModel {
    @DefaultBool
    private Boolean edited;
}
