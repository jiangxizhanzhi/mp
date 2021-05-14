package cn.cyberpeace.mp.scene.core.vo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.Data;

@Data
public abstract class BaseSceneElementVO<M extends BaseSceneElementModel> extends BaseSceneNodeVO<M> {
    private Integer type;
    private String file;
    private String systemType;
    private String systemArchitecture;
    private String systemLanguage;
    private String flavor;
    private String accessModes;
    private String initCommands;
}
