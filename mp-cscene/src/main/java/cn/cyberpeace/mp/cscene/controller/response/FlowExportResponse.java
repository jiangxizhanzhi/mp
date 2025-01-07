package cn.cyberpeace.mp.cscene.controller.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/7
 */
@Data
public class FlowExportResponse {

    @ExcelProperty("主机名称")
    @ApiModelProperty("主机名称")
    private String targetNodeName;

    @ExcelProperty("流量类型")
    @ApiModelProperty("流量类型")
    private String protocol;

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ExcelProperty("源IP")
    @ApiModelProperty("源IP")
    private String sourceIp;

    @ExcelProperty("目的IP")
    @ApiModelProperty("目的IP")
    private String targetIp;

    @ExcelProperty("源端口")
    @ApiModelProperty("源端口")
    private String sourcePort;

    @ExcelProperty("文件 (B)")
    @ApiModelProperty("文件")
    private Long size;


}
