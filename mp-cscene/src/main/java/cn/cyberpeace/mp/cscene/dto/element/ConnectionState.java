package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ConnectionState {

    @ApiModelProperty("进程ID")
    private String pid;

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("本地地址")
    private String localAddress;

    @ApiModelProperty("本地端口")
    private String localPort;

    @ApiModelProperty("远程地址")
    private String foreignAddress;

    @ApiModelProperty("远程端口")
    private String foreignPort;

    @ApiModelProperty("状态")
    private String state;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ApiModelProperty("级别")
    private int level;
}
