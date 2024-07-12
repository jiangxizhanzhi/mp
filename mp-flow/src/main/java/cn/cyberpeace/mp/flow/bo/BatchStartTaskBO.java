 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BatchStartTaskBO.java.
 // File size 430.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName BatchStartTaskBO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class BatchStartTaskBO {

    /**
     * 任务源ip集合
     */
    private List<TaskSipBO> taskSipList;


    /**
     * 执行方式：0 串行，1 并行
     */
    private int runType;

}
