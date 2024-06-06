package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Table("mp_sceneplugincloud_scene_config_element")
@NoArgsConstructor
public class CloudPluginSceneConfigElementModel extends BaseCloudPluginSceneElementModel {
    @DefaultStr
    private String place;

}
