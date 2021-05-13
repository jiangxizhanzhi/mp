package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import lombok.Data;

@Data
public class BaseSceneNodeModel extends BaseModel {
    private String scene;
    private String nodeId;
    @DefaultStr
    private String area;
    @DefaultStr
    private String icon2;
    @DefaultStr
    private String position2;
    @DefaultStr
    private String icon3;
    @DefaultStr
    private String position3;
    @DefaultStr
    private String name;
    @DefaultStr
    private String description;
}
