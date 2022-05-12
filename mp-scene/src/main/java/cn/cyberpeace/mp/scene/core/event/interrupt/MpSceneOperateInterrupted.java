package cn.cyberpeace.mp.scene.core.event.interrupt;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.Getter;

/**
 * @Description 场景事件执行中断
 * @Author maojun
 * @Date 2022/4/18
 */
public class MpSceneOperateInterrupted extends MpRuntimeException {
    @Getter
    private InterruptedStrategy strategy;

    public MpSceneOperateInterrupted(InterruptedStrategy strategy) {
        super("");
        this.strategy = strategy;
    }
}