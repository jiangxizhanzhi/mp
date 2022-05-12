package cn.cyberpeace.mp.sceneplugincollection.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sceneplugincollection_scene_log_probe_location")
public class CollectionPluginSceneLogProbeLocationModel extends BaseModel {
    private String probeId;
    private String logFormat;
    private String location;
    private String osType;
}
