package cn.cyberpeace.mp.sceneplugincloud.plugin.common;

import cn.cyberpeace.mp.sceneplugincloud.bo.base.QuickCreateParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author maojun
 * @Date 2022/5/6
 */
public class DockerInstallCommandUtils {
    private static final String DOCKER_RUN_DEFAULT = "docker run -d %s %s $repo_tag %s";

    private interface DockerParamType {
        String CMD = "cmd";
        String PORT = "port";
        String ENV = "env";
    }

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
