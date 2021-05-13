package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseSceneElementPortConnectionModel extends BaseModel {
    private String sourcePort;
    private String targetPort;
    @DefaultStr
    private String name;
    @DefaultBool
    private Boolean unidirectional;
}
