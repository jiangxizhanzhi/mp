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
    @ApiModelProperty(value = "触发类型")
    private String operationType ;
    @ApiModelProperty(value = "当前url")
    private String currentUrl ;
    @ApiModelProperty(value = "按钮文本")
    private String title ;
    @ApiModelProperty(value = "携带参数")
    private String parameter ;
    @ApiModelProperty(value = "用户id")
    private String userId ;
    @ApiModelProperty(value = "用户名称")
    private String userName ;

}
