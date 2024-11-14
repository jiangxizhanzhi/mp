// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name FlowTaskPostService.java.
// File size 4346.
// File create date Fri Jul 12 12:55:27 CST 2024.
// File update date Fri Jul 12 12:55:27 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.flow.response.BatchActuatorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 流量任务后处理逻辑
 * 负责接受到上报流量消息后的处理逻辑
 */
@Slf4j
@Service
public class FlowTaskPostService  {

    /**
     * 单个任务执行异常时的回调处理
     * @param sceneId 场景
     * @param nodeId  节点
     * @param flowId  流量
     * @param msg     错误消息
     */
    public void onFlowTaskError(String sceneId, String nodeId, String flowId, String msg) {
    }

    /**
     * 流量任务出错时，节点级别的后处理
     * @param sceneId  场景
     * @param nodeId   节点
     * @param response 执行信息
     */
    public void onNodeFlowTaskError(String sceneId, String nodeId, BatchActuatorResponse response) {
    }

    /**
     * 单个任务执行时的回调处理
     * @param sceneId 场景
     * @param nodeId  节点
     * @param flowId  流量
     */
    public void onFlowTaskOver(String sceneId, String nodeId, String flowId) {
    }

    /**
     * 单个节点上流量任务停止时的后处理操作
     * @param sceneId
     * @param nodeId
     */
    public void onStopTaskNode(String sceneId, String nodeId) {
    }
}
