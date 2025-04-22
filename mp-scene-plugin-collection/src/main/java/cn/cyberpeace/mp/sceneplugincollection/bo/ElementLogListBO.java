package cn.cyberpeace.mp.sceneplugincollection.bo;

import cn.cyberpeace.mp.sceneplugincollection.model.ElementLogListModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementLogListBO {

    @ApiModelProperty("场景id")
    private String sceneId;

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("应用名")
    private String appName;

    @ApiModelProperty("事件描述")
    private String eventDesc;

    @ApiModelProperty("级别")
    private String levelName;

    @ApiModelProperty("级别编码")
    private Integer level;

    @ApiModelProperty("rule描述信息")
    private String description;

    @ApiModelProperty("分类")
    private String catagory;

    @ApiModelProperty("日志类型用于区分日志/告警信息")
    private String logType;

    @ApiModelProperty("规则id")
    private String ruleId;

}
