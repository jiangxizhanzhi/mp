package cn.cyberpeace.mp.devops.cluster.thread;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * 实现Callable接口，来处理任务
 * @param <E>
 */
@Slf4j
public class HandleCallable<E> implements Callable<ServiceResult> {

    /**
     * 线程名称
     */
    private String threadName = "";

    /**
     * 待处理数据
     */
    private List<E> data;

    /**
     * 额外参数
     */
    private Map<String, Object> params;

    /**
     * 具体执行任务
     */
    private ITask<ServiceResult, E> task;

    public HandleCallable(String threadName, List<E> data, Map<String, Object> params,
                          ITask<ServiceResult, E> task) {
        this.threadName = threadName;
        this.data = data;
        this.params = params;
        this.task = task;
    }

    @Override
    public ServiceResult call(){
        log.info("Thread：{},total deal:{} size，start......", threadName, data.size());
        // 返回结果集
        ServiceResult serviceResult = task.execute((E) data, params);
        log.info("Thread：{},total deal:{} size，end .....", threadName, data.size());
        return serviceResult;
    }

}
