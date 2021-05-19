package cn.cyberpeace.mp.scene.core.bo.base;

import cn.cyberpeace.mp.scene.model.base.BaseSceneElementModel;
import lombok.Data;

import java.util.List;

@Data
public abstract class BaseSceneElementBO<M extends BaseSceneElementModel> extends BaseSceneNodeBO<M> {
    private Integer type;
    private String file;
    private String systemType;
    private String systemArchitecture;
    private String systemLanguage;
    private String flavor;
    private List<AccessMode> accessModes;
    private List<String> initCommands;
}
