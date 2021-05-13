package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public class BaseSceneNodeModel extends BaseModel {
    private String scene;
    private String nodeId;
    private String area;
    private String icon2;
    private String position2;
    private String icon3;
    private String position3;
    private String name;
    private String description;
}
