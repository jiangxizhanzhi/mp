package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import lombok.Data;

import java.util.Map;

@Data
public class DockerParams {
    private Boolean run;
    private Boolean interactive;
    private Boolean privileged;
    private Map<String, String> environment;
}
