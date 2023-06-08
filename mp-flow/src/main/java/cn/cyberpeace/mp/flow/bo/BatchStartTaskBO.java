package cn.cyberpeace.mp.flow.bo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 批量启动任务bo
 * @Author qiaokang
 * @Date：2023/6/8 11:41
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
