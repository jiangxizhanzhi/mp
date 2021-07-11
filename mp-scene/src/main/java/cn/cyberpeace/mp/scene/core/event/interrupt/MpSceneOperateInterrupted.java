package cn.cyberpeace.mp.scene.core.event.interrupt;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.Getter;


public class MpSceneOperateInterrupted extends MpRuntimeException {
    @Getter
    private InterruptedStrategy strategy;

    public MpSceneOperateInterrupted(InterruptedStrategy strategy) {
        super("");
        this.strategy = strategy;
    }
}