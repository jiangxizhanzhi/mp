 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ISystemStatusInfo.java.
 // File size 486.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
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
