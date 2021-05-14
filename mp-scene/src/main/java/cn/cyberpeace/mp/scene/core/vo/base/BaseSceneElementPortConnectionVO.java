package cn.cyberpeace.mp.scene.core.vo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementPortConnectionModel;
import lombok.Data;

@Data
public abstract class BaseSceneElementPortConnectionVO<M extends BaseSceneElementPortConnectionModel> extends EidVO<M> {
    private String sourcePort;
    private String targetPort;
    private String name;
    private Boolean unidirectional;
}
