package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import lombok.Data;

@Data
public class DockerParams {
    private Boolean run;
    private Boolean interactive;
    private Boolean privileged;
    private String environmentStr;
    private Boolean tty;
    private String hostConfigExStr;
    private Boolean options;
}
