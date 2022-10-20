package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogVO {
    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "时间")
    private LocalDateTime time;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "战队名称")
    private String team;

    @ApiModelProperty(value = "设备类型")
    private String equipmentType;

    @ApiModelProperty(value = "设备名称")
    private String equipment;
}
