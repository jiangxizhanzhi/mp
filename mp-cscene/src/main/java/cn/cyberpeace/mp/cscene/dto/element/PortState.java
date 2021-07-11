package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class PortState {

    @ApiModelProperty("进程号")
    private String pid;

    @ApiModelProperty("进程名称")
    private String process;

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("本地地址")
    private String localIp;

    @ApiModelProperty("端口号")
    private String port;

    @ApiModelProperty("远程地址")
    private String remoteIp;

    @ApiModelProperty("远程端口")
    private String remotePort;

    @ApiModelProperty("状态")
    private String state;

}
