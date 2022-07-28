package cn.cyberpeace.mp.devops.cluster.constant;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 16:26
 */
public class MonitorConstant {

    public static final String OP_STATUS = "status";

    public static final String OP_RESTART = "restart";

    public static final String OS_TYPE_LINUX = "linux";

    public static final String BIN_TOOLS_DIR = "/var/ossec/bin/tools";

    // 系统告警定时任务运行间隔 单位s
    public static final Integer ALARM_TASK_RATE = 60 * 5;

}
