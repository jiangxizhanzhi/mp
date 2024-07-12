 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseSceneEventModel.java.
 // File size 882.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.scene.core.event.base;

/**
 * @ClassName BaseSceneEventModel
 * @Description 通用事件模型
 * event: 事件（目标做某件事）
 * eventStatus: 事件状态（当前这件事的状态）
 * process: 事件进程（这件事进行到某个阶段）
 * processStatus: 事件进程状态（这件事在这个阶段的状态）
 * processMessage: 进程信息（这件事在这个阶段状态的具体说明信息）
 * stage: 阶段
 * stage: 阶段百分比
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseEventModel
 */
public interface BaseSceneEventModel extends BaseEventModel {

    void setStage(String stage);

    String getStage();

    void setStageName(String stageName);

    String getStageName();

    void setStagePercentage(Integer percentage);

    Integer getStagePercentage();
}
