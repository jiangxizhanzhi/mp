package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DefaultPoolTask extends PoolTask<Object> {
    public DefaultPoolTask(Runnable task) {
        super(task);
    }
}
