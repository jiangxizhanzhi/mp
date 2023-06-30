package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.util.thread.taskpool.ThreadPoolInfo;

import java.util.List;

/**
 * @author wangssh
 * @version 1.0
 * @description TODO
 * @date 2023/6/29
 **/
public interface ISystemStatusInfo {

    /**
     * 获取线程池信息
     * @description: 获取线程池信息
     * @return: 信息状态信息列表
     * @date: 2023/6/29
     */
    public List<ThreadPoolInfo> getThreadPoolInfos();

}
