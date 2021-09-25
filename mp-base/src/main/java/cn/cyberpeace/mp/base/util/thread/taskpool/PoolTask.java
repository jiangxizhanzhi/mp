package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PoolTask<C> extends BasePoolTask<C, Runnable, Object> {
    public PoolTask(Runnable task) {
        setTask(task);
    }

    @Override
    public void invoke() {
        getTask().run();
    }
}
