package cn.cyberpeace.mp.base.util.thread.taskpool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author wangssh
 * @version 1.0
 * @description TODO
 * @date 2023/6/29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ThreadPoolInfo {

    public String threadPoolName;

    private int coreThreadSize;

    private int maxThreadSize;

    private int currentThreadSize;

    private int activeThreadSize;

}
