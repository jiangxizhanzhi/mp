package cn.cyberpeace.mp.devopscluster.constant;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/14 16:26
 */
public class MonitorConstant {

    public static final String OP_STATUS = "status";

    public static final String OP_RESTART = "restart";

    public static final String OS_TYPE_LINUX = "linux";

    public static final String MONITOR_CMD = "/var/ossec/bin/tools/python27/python /var/ossec/bin/tools/%s.py %s %s";

}
