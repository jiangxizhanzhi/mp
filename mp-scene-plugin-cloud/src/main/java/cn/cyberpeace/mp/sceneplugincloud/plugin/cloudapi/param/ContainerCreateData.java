 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ContainerCreateData.java.
 // File size 1214.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ContainerCreateData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see CloudHttpData
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContainerCreateData implements CloudHttpData {
    private String name;
    private String image;
    private Integer cpu;
    private Integer ram;
    private Integer disk;
    private String requestUser;
    private String scene;
    private String department;
    private String runHost;
    private List<VirtualNic> network;

    private Boolean interactive;
    private Boolean tty;
    private Boolean run;
    private Boolean privileged;
    private Boolean keepNicsSequence;
    private String command;
    private String postCommand;
    private String workdir;
    private Map<String, String> environment;
    private String hostname;

    private String id;
    private List<String> envs;
}
