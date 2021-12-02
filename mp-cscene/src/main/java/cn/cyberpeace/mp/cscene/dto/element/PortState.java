package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/2 15:39
 * 端口：端口号   协议  pid
 * 网络连接：  协议  本机地址  远程地址 状态
 */
@Data
public class PortState {

    @ApiModelProperty("进程号")
    private String pid;

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("端口号")
    private String port;

}
