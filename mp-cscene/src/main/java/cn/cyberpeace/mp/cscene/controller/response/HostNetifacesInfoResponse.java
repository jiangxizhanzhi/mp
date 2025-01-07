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
public class HostNetifacesInfoResponse {

    @ExcelProperty(value = "名称")
    @ApiModelProperty(value = "名称")
    private String name;

    @ExcelProperty(value = "类型")
    @ApiModelProperty(value = "类型")
    private String type;

    @ExcelProperty(value = "链路状态")
    @ApiModelProperty(value = "链路状态")
    private String state;

    @ExcelProperty(value = "mtu")
    @ApiModelProperty(value = "mtu")
    private Integer mtu;

    @ExcelProperty(value = "MAC地址")
    @ApiModelProperty(value = "mac地址")
    private String mac;

    @ExcelProperty(value = "IP地址")
    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ExcelProperty(value = "TX(pkts)")
    @ApiModelProperty(value = "TX(pkts)")
    private String txPkts;

    @ExcelProperty(value = "RX(pkts)")
    @ApiModelProperty(value = "RX(pkts)")
    private String rxPkts;

    @ExcelProperty(value = "TX(bytes)")
    @ApiModelProperty(value = "TX(bytes)")
    private Integer txBytes;

    @ExcelProperty(value = "RX(bytes)")
    @ApiModelProperty(value = "RX(bytes)")
    private Integer rxBytes;


}
