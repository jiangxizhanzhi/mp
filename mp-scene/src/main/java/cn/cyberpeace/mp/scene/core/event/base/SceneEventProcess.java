 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SceneEventProcess.java.
 // File size 529.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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
