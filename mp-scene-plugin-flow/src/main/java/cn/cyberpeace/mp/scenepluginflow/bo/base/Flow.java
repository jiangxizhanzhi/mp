package cn.cyberpeace.mp.scenepluginflow.bo.base;

import cn.cyberpeace.mp.flow.enums.InteractiveTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Data
public class Flow {
    @ApiModelProperty(value = "流量id")
    private String id;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "描述")
    private String desc;
    @ApiModelProperty(value = "分类")
    private String type;
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    private Integer interactiveType;
    @ApiModelProperty(value = "执行次数")
    private Integer count;
    @ApiModelProperty(value = "状态")
    private Integer status;
    @ApiModelProperty(value = "状态描述")
    private String statusText;
    @ApiModelProperty(value = "流量任务id")
    private String taskId;
    @ApiModelProperty(value = "流量脚本参数列表")
    private List<FlowScriptParam> scriptParamModels;

    public Integer getStatus() {
        if (Objects.isNull(status)) {
            status = Status.INIT;
        }
        return status;
    }

    public Integer getInteractiveType() {
        // 针对没有interactiveType的老数据，暂时给个默认值
        if (Objects.isNull(interactiveType)) {
            interactiveType = InteractiveTypeEnum.BACKGROUND.toValue();
        }
        return interactiveType;
    }

    public static class Status {
        public static final int INIT = -1;
        public static final int OFF = 0;
        public static final int ON = 1;
        public static final int ERROR = 2;
        public static final int PREPARING = 3;

        private Status() {
        }
    }

    @Setter
    @Getter
    public static class FlowScriptParam {
        @ApiModelProperty(value = "参数key")
        private String paramKey;
        @ApiModelProperty(value = "参数值")
        private String paramValue;
        @ApiModelProperty(value = "参数类型：0 string,1 IP,2 number,3 bool")
        private Integer paramType;
        @ApiModelProperty(value = "参数描述")
        private String paramDesc;
    }
}
