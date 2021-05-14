package cn.cyberpeace.mp.scene.core.vo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import lombok.Data;

@Data
public abstract class BaseSceneVO<M extends BaseSceneModel> extends EidVO<M> {
    private String name;
    private String description;
    private String file;
}
