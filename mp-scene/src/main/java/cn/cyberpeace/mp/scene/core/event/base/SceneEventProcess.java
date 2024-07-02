package cn.cyberpeace.mp.scene.core.event.base;

import lombok.Data;

/**
 * @ClassName SceneEventProcess
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see EventProcess
 */
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
