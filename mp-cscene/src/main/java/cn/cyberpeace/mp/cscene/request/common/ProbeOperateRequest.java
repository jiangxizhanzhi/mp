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

    @ApiModelProperty("探针类型,1:变更事件;2:原始日志;3:日志事件;4:自定义采集")
    @NotNull
    private Integer type;

    @ApiModelProperty("0:关闭,1:开启")
    @NotNull
    private Integer operate;

}
