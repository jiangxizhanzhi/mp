package cn.cyberpeace.mp.sceneplugincollection.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Probe {
    @ApiModelProperty(value = "探针id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String desc;
    @ApiModelProperty(value = "分类")
    private String type;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "状态描述")
    private String statusText;

    public static class Status {
        public static final int OFF = 0;
        public static final int ON = 1;
        public static final int ERROR = 2;

        private Status() {}
    }
}
