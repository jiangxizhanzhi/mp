package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.sceneplugincollection.model.base.BaseCollectionPluginSceneModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CollectionPluginSceneModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseCollectionPluginSceneModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincollection_scene")
public class CollectionPluginSceneModel extends BaseCollectionPluginSceneModel {
    @DefaultStr
    private String flowCollector;

    @DefaultStr
    private String flowCollectorContainerId;

    @DefaultStr
    private String probeCollector;

    @DefaultStr
    private String probeRouter;

    @DefaultStr
    private String flowFile;

    @DefaultStr
    private String flowMirrorId;

    @DefaultStr
    private String optionKey;

}
