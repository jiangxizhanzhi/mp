package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.scene.core.plugin.model.BasePluginSceneElementModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table("mp_sceneplugincollection_scene_cycle_data")
public class SceneCycleDataModel extends BasePluginSceneElementModel {
    @DefaultStr
    private String data;
    @DefaultInt
    private String type;

    public static final String PORT = "1";
    public static final String PROCESS = "2";
    public static final String NET = "3";
    public static final String FILE = "4";
    public static final String REGISTRY = "5";
    public static final String LOG = "6";
    public static final String CONNECTION = "7";
    public static final String HOST = "8";
}
