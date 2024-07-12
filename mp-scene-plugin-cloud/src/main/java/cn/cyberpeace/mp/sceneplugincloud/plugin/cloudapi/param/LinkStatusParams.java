 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name LinkStatusParams.java.
 // File size 472.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName LinkStatusParams
 * @Description
 * @Author maojun
 * @Date 2022/7/8
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Getter
@Setter
public class LinkStatusParams implements CloudHttpParam {
    private String networkId;
    private String instanceId;
}
