package cn.cyberpeace.mp.sceneplugincollection.service.param;

import lombok.Data;

import java.util.Set;

@Data
public class SceneControlProbeOperate {
    private Set<String> collectIps;
    private boolean changeEventState;
    private boolean originLog;
    private boolean logEvent;
    private boolean customCollection;
    private boolean collectPeriod;
}