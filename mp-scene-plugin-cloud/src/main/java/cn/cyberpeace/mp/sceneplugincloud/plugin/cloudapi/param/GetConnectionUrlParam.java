 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name GetConnectionUrlParam.java.
 // File size 507.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpParam;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName GetConnectionUrlParam
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpParam
 */
@Data
@Accessors(chain = true)
public class GetConnectionUrlParam implements CloudHttpParam {
    private String vmId;
    private String consoleType;
}