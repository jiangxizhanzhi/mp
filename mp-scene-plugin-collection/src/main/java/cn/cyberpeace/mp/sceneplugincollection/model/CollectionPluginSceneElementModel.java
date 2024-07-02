package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName CollectionPluginSceneElementModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneElementModel
 */
@Data
@AllArgsConstructor
@Table("mp_sceneplugincollection_scene_element")
public class CollectionPluginSceneElementModel extends BaseCollectionPluginSceneElementModel {
}
