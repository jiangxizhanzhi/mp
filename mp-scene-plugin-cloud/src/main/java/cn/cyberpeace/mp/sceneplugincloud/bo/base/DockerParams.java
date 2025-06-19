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
    /**
     * 镜像来源，1：基础镜像，2：CP-CLOUD已有镜像，3：自定义上传
     * 用来给前端判断，是否要展示高级选项相关的输入框
     */
    private Integer imageSource;
}
