package cn.cyberpeace.mp.flow.bo;

import lombok.Data;

/**
 * @Description: 任务源ip对象
 * @Author qiaokang
 * @Date：2023/6/8 11:42
 */
@Data
public class TaskSipBO {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 源ip
     */
    private String sourceIp;

}
