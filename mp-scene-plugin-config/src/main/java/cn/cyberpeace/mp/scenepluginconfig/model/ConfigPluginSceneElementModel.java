package cn.cyberpeace.mp.scenepluginconfig.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultLong;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scenepluginconfig.model.base.BaseConfigPluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_scenepluginconfig_scene_element")
public class ConfigPluginSceneElementModel extends BaseConfigPluginSceneElementModel {
    @DefaultLong
    private Long createConsumeSeconds;
}
