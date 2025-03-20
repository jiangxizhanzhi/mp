package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlowMirrorBO {
    private String groupName;
    private String mirrorId;
    private Integer optionKey;
    private List<Source> sources;

    // target 和 targetIp互斥，配置了targetIp则不能配置target
    private Target target;
    private String targetIp;

    @Getter
    @Setter
    public static class Source {
        private String nodeId;
    }

    @Getter
    @Setter
    public static class Target {
        private String nodeId;
        private String portId;
    }

}