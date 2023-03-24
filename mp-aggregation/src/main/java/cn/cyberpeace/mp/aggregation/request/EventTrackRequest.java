package cn.cyberpeace.mp.aggregation.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventTrackRequest {
    @ApiModelProperty(value = "模块")
    private String module;
    @ApiModelProperty(value = "分类")
    private String category;
    @ApiModelProperty(value = "动作")
    private String action;
    @ApiModelProperty(value = "操作对象")
    private String target;
}
