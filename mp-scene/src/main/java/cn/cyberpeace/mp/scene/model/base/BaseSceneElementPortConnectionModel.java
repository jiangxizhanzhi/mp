package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public class BaseSceneElementPortConnectionModel extends BaseModel {
    private String sourcePort;
    private String targetPort;
    private String name;
    private Boolean unidirectional;
}
