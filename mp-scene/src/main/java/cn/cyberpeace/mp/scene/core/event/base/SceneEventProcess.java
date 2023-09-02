package cn.cyberpeace.mp.scene.core.event.base;

import lombok.Data;

@Data
public class SceneEventProcess extends EventProcess {
    private String stage;
    private int order;

    public SceneEventProcess(String process, String processName, String stage, int order) {
        super(process, processName);
        this.stage = stage;
        this.order = order;
    }
}
