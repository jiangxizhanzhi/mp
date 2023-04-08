package cn.cyberpeace.mp.cscene.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
@Builder
public class ProbeEventState {
    @ApiModelProperty("变更事件开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer changeEventState;

    @ApiModelProperty("原始日志开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer originLog;

    @ApiModelProperty("日志事件开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer logEvent;

    @ApiModelProperty("自定义采集开关状态, 0:关闭,1:开启")
    @NotNull
    private Integer customCollection;
}
