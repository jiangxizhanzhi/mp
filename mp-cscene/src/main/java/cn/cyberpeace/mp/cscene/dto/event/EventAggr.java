package cn.cyberpeace.mp.cscene.dto.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class EventAggr {

    @ApiModelProperty("进程百分比")
    private double process;

    @ApiModelProperty("文件百分比")
    private double file;

    @ApiModelProperty("端口百分比")
    private double port;

    @ApiModelProperty("网络连接百分比")
    private double network;

    @ApiModelProperty("用户百分比")
    private double user;
}
