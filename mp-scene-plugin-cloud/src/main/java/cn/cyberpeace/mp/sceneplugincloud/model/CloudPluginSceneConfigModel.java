package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName CloudPluginSceneConfigModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCloudPluginSceneModel
 */
@Data
@AllArgsConstructor
@Table("mp_sceneplugincloud_scene_config")
public class CloudPluginSceneConfigModel extends BaseCloudPluginSceneModel {
}
