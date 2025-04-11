package cn.cyberpeace.mp.cscene.bo;

import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionPluginSceneElementBO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CsceneElementCollectInfo extends CollectionPluginSceneElementBO {
    private String nodeId;
    private String nodeName;
}
