package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneModel;
import lombok.Data;

@Data
public abstract class BaseSceneBO<M extends BaseSceneModel, T extends BaseSceneBO<M, T>> extends EidBO<M, T> {
    private String name;
    private String description;
    private String file;
}
