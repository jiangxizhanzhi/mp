 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualRouterGetData.java.
 // File size 771.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName VirtualRouterGetData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@NoArgsConstructor
public class VirtualRouterGetData implements CloudHttpData {
    private String id;
    private String name;
    private String description;
    private Object externalGateway;
    private List<Object> routes;
    private List<Object> interfaces;
    private String project;
    private String status;
    private String updatedAt;
    private String createdAt;
}
