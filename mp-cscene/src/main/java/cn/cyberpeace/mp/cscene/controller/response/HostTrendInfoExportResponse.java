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
public class HostTrendInfoExportResponse {

    @ExcelProperty("时间")
    @ApiModelProperty("时间")
    private LocalDateTime time;

    @ExcelProperty("CPU占用率(%)")
    @ApiModelProperty("cpu使用率")
    private Double cpuPercent;

    @ExcelProperty("内存占用率(%)")
    @ApiModelProperty("内存使用率")
    private Double memoryPercent;

    @ExcelProperty("磁盘占用率(%)")
    @ApiModelProperty("磁盘使用率")
    private Double diskPercent;
}
