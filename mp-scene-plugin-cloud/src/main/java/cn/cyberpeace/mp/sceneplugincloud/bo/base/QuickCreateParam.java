package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuickCreateParam {
    @ApiModelProperty(value = "id")
    private String uuid;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "logo")
    private String logo;
    @ApiModelProperty(value = "分类")
    private String categories;
    @ApiModelProperty(value = "部署类型")
    private Integer deployType;
    @ApiModelProperty(value = "资源文件列表")
    private List<Resource> resourceList;
    @ApiModelProperty(value = "docker资源信息")
    private DockerInstaller dockerInstallerData;

    @Data
    public static class Resource {
        @ApiModelProperty(value = "文件地址")
        private String file;
        @ApiModelProperty(value = "支持平台")
        private String platforms;
        @ApiModelProperty(value = "用户文件名")
        private String userFilename;
        @ApiModelProperty(value = "临时文件id")
        private String tmpdirUuid;
    }

    @Data
    public static class DockerInstaller {
        @ApiModelProperty(value = "文件地址")
        private String dockerUrl;
        @ApiModelProperty(value = "支持平台")
        private List<StartUpParameter> startUpParameterList;
        @ApiModelProperty(value = "用户文件名")
        private String userFilename;
        @ApiModelProperty(value = "临时文件id")
        private String tmpdirUuid;

        @Data
        public static class StartUpParameter {
            @ApiModelProperty(value = "参数名称")
            private String name;
            @ApiModelProperty(value = "参数说明")
            private String desc;
            @ApiModelProperty(value = "参数类型")
            private String type;
            @ApiModelProperty(value = "是否必须")
            private String required;
            @ApiModelProperty(value = "默认值")
            private String defaultVal;

            @ApiModelProperty(value = "主机端口")
            private String hostPort;
            @ApiModelProperty(value = "容器端口")
            private String containerPort;
            @ApiModelProperty(value = "命令")
            private String cmd;
        }
    }
}
