package cn.cyberpeace.mp.log.response;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunTimeLogResponse {
    @ApiModelProperty(value = "日志名称", required = true)
    private String logName;

    @ApiModelProperty(value = "日志文件大小", required = true)
    private Long size;

    @ApiModelProperty(value = "日志文件最新时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime lastModifyTime;
}
