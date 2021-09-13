package cn.cyberpeace.mp.devops.log.response;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.*;

/**
  *@Desciption 查询日志
  *@Author zhangjin
  *@Date 9/13/21 9:30 上午
**/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RunTimeLogResponse {
    @ApiModelProperty(value = "日志名称", required = true)
    private String logName;

    @ApiModelProperty(value = "日志文件大小", required = true)
    private Integer size;

    @ApiModelProperty(value = "日志文件最新时间-yyyy-MM-dd HH:mm:ss", required = true)
    private LocalDateTime lastModifyTime;
}
