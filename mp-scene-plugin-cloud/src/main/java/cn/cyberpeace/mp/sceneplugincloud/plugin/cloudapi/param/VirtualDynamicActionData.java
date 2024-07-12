 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualDynamicActionData.java.
 // File size 830.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;

/**
 * @ClassName VirtualDynamicActionData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
public class VirtualDynamicActionData implements CloudHttpData {

    public final static String RESTART = "restart";
    public final static String START = "resume";
    public final static String STOP = "suspend";
    public final static String RESUME = "resume";
    public final static String SUSPEND = "suspend";
    public final static String PAUSE = "pause";
    public final static String UNPAUSE = "unpause";
    public final static String REBOOT = "reboot";

    private String action;
}
