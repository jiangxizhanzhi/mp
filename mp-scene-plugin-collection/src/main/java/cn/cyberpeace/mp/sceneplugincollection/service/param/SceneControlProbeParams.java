package cn.cyberpeace.mp.sceneplugincollection.service.param;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SceneControlProbeParams {
    private String sceneId;
    List<SceneElementControlProbeParams> elementControlProbeParams;

    public SceneControlProbeParams(String sceneId) {
        this.sceneId = sceneId;
        this.elementControlProbeParams = new ArrayList<>();
    }

    @Data
    public static class SceneElementControlProbeParams {
        private String elementId;
        private boolean enable;
        private ChangeEventParams changeEventParams;
        private OriginLogParams originLogParams;
        private LogEventParams logEventParams;
        private CustomCollectionParams customCollectionParams;

        public SceneElementControlProbeParams(String elementId, boolean enable) {
            this.elementId = elementId;
            this.enable = enable;
        }
    }

    @Data
    public static class ChangeEventParams {
        private boolean enable;

        public ChangeEventParams(boolean enable) {
            this.enable = enable;
        }
    }

    @Data
    public static class OriginLogParams {
        private boolean enable;

        public OriginLogParams(boolean enable) {
            this.enable = enable;
        }
    }

    @Data
    public static class LogEventParams {
        private boolean enable;

        public LogEventParams(boolean enable) {
            this.enable = enable;
        }
    }

    @Data
    public static class CustomCollectionParams {
        private boolean enable;

        public CustomCollectionParams(boolean enable) {
            this.enable = enable;
        }
    }
}