package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProbeOperateRequest {

    @ApiModelProperty("场景id")
    @NotNull
    private String sceneId;

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
