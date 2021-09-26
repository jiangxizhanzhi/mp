package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

@Data
@NoArgsConstructor
public class DefaultResultPoolTask<R> extends ResultPoolTask<Object, R> {
    public DefaultResultPoolTask(Callable<R> task) {
        super(task);
    }
}
