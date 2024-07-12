 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name HandleCallable.java.
 // File size 1680.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.thread;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * @ClassName HandleCallable
 * @Description 实现Callable接口，来处理任务
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Callable
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

    /**
     * HandleCallable
     *
     * @param threadName
     * @param data
     * @param params
     * @param task
     */
    public HandleCallable(String threadName, List<E> data, Map<String, Object> params,
                          ITask<ServiceResult, E> task) {
        this.threadName = threadName;
        this.data = data;
        this.params = params;
        this.task = task;
    }

    /**
     * call
     *
     * @return 统一服务返回结果
     */
    @Override
    public ServiceResult call(){
        log.info("Thread：{},total deal:{} size，start......", threadName, data.size());
        // 返回结果集
        ServiceResult serviceResult = task.execute((E) data, params);
        log.info("Thread：{},total deal:{} size，end .....", threadName, data.size());
        return serviceResult;
    }

}
