package cn.cyberpeace.mp.log.dto;

import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogReportDTO {

    
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;

    
    @ApiModelProperty(value = "下载地址")
    private String downloadUrl;

    
    @ApiModelProperty(value = "节点ip")
    private String hostIp;

    
    @ApiModelProperty(value = "日志类型")
    private LogReportTypeEnum logReportTypeEnum;

    @ApiModelProperty(value = "更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
