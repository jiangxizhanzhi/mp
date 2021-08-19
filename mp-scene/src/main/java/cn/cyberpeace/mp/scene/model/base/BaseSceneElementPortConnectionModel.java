package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.UuidGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseSceneElementPortConnectionModel extends BaseModel {
    private String scene;
    private String sourceElement;
    private String targetElement;
    private String sourcePort;
    private String targetPort;
    @DefaultGen(UuidGenerator.class)
    private String connectionId;
    private String sourcePortId;
    private String targetPortId;
    @DefaultStr
    private String name;
    @DefaultBool
    private Boolean unidirectional;
}
