 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ISystemStatusInfo.java.
 // File size 523.
 // File create date Fri Jul 12 12:55:30 CST 2024.
 // File update date Fri Jul 12 12:55:30 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service;

import cn.cyberpeace.mp.base.util.thread.taskpool.ThreadPoolInfo;

import java.util.List;

/**
 * @ClassName ISystemStatusInfo
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface ISystemStatusInfo {

    /**
     * 获取线程池信息
     *
     * @description: 获取线程池信息
     * @return: 信息状态信息列表
     * @date: 2023/6/29
     */
    public List<ThreadPoolInfo> getThreadPoolInfos();

}
