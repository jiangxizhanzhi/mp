package cn.cyberpeace.mp.cscene.controller.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/2
 */
@Data
public class HostAcquisitionProbeExportResponse {

    @ExcelProperty("主机名称")
    @ApiModelProperty("主机名称")
    private String nodeName;

    @ExcelProperty("探针名称")
    @ApiModelProperty("探针名称")
    private String probeName;

    @ExcelProperty("详情")
    @ApiModelProperty("详情")
    private String content;

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime time;

}
