package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.util.thread.taskpool.ThreadPoolInfo;

import java.util.List;


public interface ISystemStatusInfo {

    
    public List<ThreadPoolInfo> getThreadPoolInfos();

}
