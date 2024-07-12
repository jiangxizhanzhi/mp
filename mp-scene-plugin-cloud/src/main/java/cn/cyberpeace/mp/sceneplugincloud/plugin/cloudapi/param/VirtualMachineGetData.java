 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name VirtualMachineGetData.java.
 // File size 1027.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName VirtualMachineGetData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@NoArgsConstructor
public class VirtualMachineGetData implements CloudHttpData {
    private String id;
    private String name;
    private String uuid;
    private String description;
    private String image;
    private String flavor;
    private Object addresses;
    private String volumes;
    private String userData;
    private Object metadata;
    private String status;
    private String host;
    private String instanceName;
    private String hostname;
    private String user;
    private String project;
    private String updatedAt;
    private String createdAt;
}
