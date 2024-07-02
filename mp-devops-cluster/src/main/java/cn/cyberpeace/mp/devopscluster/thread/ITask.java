package cn.cyberpeace.mp.devopscluster.thread;


import java.util.Map;

/**
 * @ClassName ITask
 * @Description 任务处理接口，具体业务逻辑可实现该接口
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
