 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DockerInstallCommandUtils.java.
 // File size 1945.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.sceneplugincloud.bo.base.QuickCreateParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DockerInstallCommandUtils
 * @Description
 * @Author maojun
 * @Date 2022/5/6
 * @Version 1.0.0
 */
public class DockerInstallCommandUtils {
    private static final String DOCKER_RUN_DEFAULT = "docker run -d %s %s $repo_tag %s";

    private interface DockerParamType {
        String CMD = "cmd";
        String PORT = "port";
        String ENV = "env";
    }

    /**
     * genDockerRunDefaultCmd
     *
     * @param params
     * @return
     */
    public static String genDockerRunDefaultCmd(List<QuickCreateParam.DockerInstaller.StartUpParameter> params) {
        List<String> ports = new ArrayList<>();
        List<String> envs = new ArrayList<>();
        List<String> cmds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(params)) {
            params.forEach(param -> {
                if (DockerParamType.CMD.equals(param.getType())) {
                    cmds.add(param.getCmd());
                } else if (DockerParamType.PORT.equals(param.getType())) {
                    ports.add(String.format("-p %s:%s", param.getHostPort(), param.getContainerPort()));
                } else if (DockerParamType.ENV.equals(param.getType())) {
                    envs.add(String.format("-e %s=%s", param.getName(), param.getDefaultVal()));
                }
            });
        }
        String cmd = String.format(
                DOCKER_RUN_DEFAULT,
                StringUtils.join(ports, StringUtils.SPACE),
                StringUtils.join(envs, StringUtils.SPACE),
                StringUtils.join(cmds, StringUtils.SPACE)
        );
        return cmd;
    }
}
