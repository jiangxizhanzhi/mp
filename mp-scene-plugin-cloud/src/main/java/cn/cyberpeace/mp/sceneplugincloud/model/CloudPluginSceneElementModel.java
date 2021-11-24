package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincloud.model.base.BaseCloudPluginSceneElementModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Table("mp_sceneplugincloud_scene_element")
public class CloudPluginSceneElementModel extends BaseCloudPluginSceneElementModel {
    // 宿主机信息
    @DefaultStr
    private String host;

    public CloudPluginSceneElementModel() {

    }
}
