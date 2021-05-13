package cn.cyberpeace.mp.scene.model.base;

import lombok.Data;

@Data
public class BaseSceneElementModel extends BaseSceneNodeModel {
    private Integer type;
    private String file;
    private String systemType;
    private String systemArchitecture;
    private String systemLanguage;
    private String flavor;
    private String accessModes;
    private String initCommands;
}
