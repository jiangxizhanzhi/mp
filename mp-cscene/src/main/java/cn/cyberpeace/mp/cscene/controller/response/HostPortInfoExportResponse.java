package cn.cyberpeace.mp.cscene.controller.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/2
 */
@Data
public class HostPortInfoExportResponse {

    @ExcelProperty("协议")
    @ApiModelProperty(value = "协议")
    private String protocol;

    @ExcelProperty("本机地址")
    @ApiModelProperty("本机地址")
    private String localIp;

    @ExcelProperty("本机端口")
    @ApiModelProperty("本机端口")
    private Integer localPort;

    @ExcelProperty("远程地址")
    @ApiModelProperty("远程地址")
    private String remoteIp;

    @ExcelProperty("远程端口")
    @ApiModelProperty("远程端口")
    private Integer remotePort;

    @ExcelProperty("状态")
    @ApiModelProperty(value = "状态")
    private String state;

    @ExcelProperty("进程")
    @ApiModelProperty(value = "进程")
    private String process;

}
