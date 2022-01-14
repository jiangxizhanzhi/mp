package cn.cyberpeace.mp.devops.cluster.thread;


import java.util.Map;

/**
 * 任务处理接口，具体业务逻辑可实现该接口
 * @param <R> 返回值类型
 * @param <T> 入参类型
 */
public interface ITask<R,T> {

    /**
     * 任务执行方法
     * @param e
     * @param params 额外任务参数
     * @return
     */
    R execute(T e, Map<String, Object> params);
}
