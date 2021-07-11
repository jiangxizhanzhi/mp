package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class Probe {
    @ApiModelProperty(value = "探针id")
    private String id;
    @ApiModelProperty(value = "是否通用探针")
    private Boolean common;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String desc;
    @ApiModelProperty(value = "分类 0:信息类 1:事件类 2:日志类")
    private String type;
    @ApiModelProperty(value = "探针驱动类型")
    private Integer execType;
    @ApiModelProperty(value = "系统类型 0:Linux 1:Windows")
    private String osType;
    @ApiModelProperty(value = "参数")
    private List<Param> params;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "状态描述")
    private String statusText;
    @ApiModelProperty(value = "采集时间间隔,单位:秒")
    private Integer interval;
    @ApiModelProperty(value = "是否开启探针")
    private Boolean disabled;
    @ApiModelProperty(value = "探针数据输出格式 json plain none")
    private String outputFormat;
    @ApiModelProperty(value = "探针脚本类型 0:shell 1:python 2:bat")
    private Integer scriptType;
    @ApiModelProperty(value = "探针脚本文件地址")
    private String scriptUrl;
    @ApiModelProperty(value = "探针采集路径")
    private List<String> collectPath;

    @Data
    public static class Param {
        @ApiModelProperty(value = "名称")
        private String name;
        @ApiModelProperty(value = "类型")
        private String type;
        @ApiModelProperty(value = "说明")
        private String description;
        @ApiModelProperty(value = "默认值")
        private String defaultValue;
        @ApiModelProperty(value = "自定义值")
        private String value;
    }

    public static class Status {
        public static final int OFF = 0;
        public static final int ON = 1;
        public static final int ERROR = 2;

        private Status() {}
    }

    
    public static class ExecType {
        public static final int BASE_PROBE = 0;
        public static final int LOG_PROBE = 1;
        public static final int FILE_PROBE = 2;
        public static final int REGISTRY_PROBE = 3;
        public static final int SCRIPT_PROBE = 4;

        private ExecType() {
        }
    }
}
