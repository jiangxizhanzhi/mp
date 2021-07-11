package cn.cyberpeace.mp.devopscluster.thread;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;



@Slf4j
public class HandleCallable<E> implements Callable<ServiceResult> {

    
    private String threadName = "";

    
    private List<E> data;

    
    private Map<String, Object> params;

    
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
        
        ServiceResult serviceResult = task.execute((E) data, params);
        log.info("Thread：{},total deal:{} size，end .....", threadName, data.size());
        return serviceResult;
    }

}
