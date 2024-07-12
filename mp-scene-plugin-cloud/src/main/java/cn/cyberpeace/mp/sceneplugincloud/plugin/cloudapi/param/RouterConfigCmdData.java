 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RouterConfigCmdData.java.
 // File size 625.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName RouterConfigCmdData
 * @Description
 * @Author maojun
 * @Date 2022/4/19
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Getter
@Setter
public class RouterConfigCmdData implements CloudHttpData {
    private String serialIp;
    private Integer serialPort;
    private List<String> cmds;
    private String cmd;

    private String vendor;
    private List<String> commands;
}
