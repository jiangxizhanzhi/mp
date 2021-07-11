package cn.cyberpeace.mp.log.dto;

import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunTimeLogDTO {

    @ApiModelProperty(value = "日志名称", required = true)
    private String logName;

    @ApiModelProperty(value = "日志文件大小", required = true)
    private Long size;

    
    @ApiModelProperty(value = "id")
    private String id;

    
    @ApiModelProperty(value = "下载地址")
    private String downloadUrl;

    
    @ApiModelProperty(value = "节点ip")
    private String hostIp;

    
    @ApiModelProperty(value = "日志类型")
    private LogReportTypeEnum logReportTypeEnum;

    @ApiModelProperty(value = "日志文件最新时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime lastModifyTime;

}
