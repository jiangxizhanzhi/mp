 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name MonitorConstant.java.
 // File size 506.
 // File create date Fri Jul 12 12:55:28 CST 2024.
 // File update date Fri Jul 12 12:55:28 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.constant;

/**
 * @ClassName MonitorConstant
 * @Description
 * @Author zhouxinghuai
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class MonitorConstant {

    public static final String OP_STATUS = "status";

    public static final String OP_RESTART = "restart";

    public static final String OS_TYPE_LINUX = "linux";

    public static final String MONITOR_CMD = "/var/ossec/bin/tools/python27/python /var/ossec/bin/tools/%s.py %s %s";

}
