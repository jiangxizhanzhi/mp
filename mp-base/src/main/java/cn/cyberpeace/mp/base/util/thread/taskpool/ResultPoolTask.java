package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Callable;

@Data
@NoArgsConstructor
public class ResultPoolTask<C, R> extends BasePoolTask<C, Callable<R>, R> {
    public ResultPoolTask(Callable<R> task) {
        setTask(task);
    }

    @Override
    public void invoke() throws Exception {
        setResult(getTask().call());
    }
}
