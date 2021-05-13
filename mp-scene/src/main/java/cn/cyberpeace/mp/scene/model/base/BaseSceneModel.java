package cn.cyberpeace.mp.scene.model.base;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

@Data
public class BaseSceneModel extends BaseModel {
    private String source;
    private String name;
    private String description;
    private String file;
    private Boolean deleted;
}
