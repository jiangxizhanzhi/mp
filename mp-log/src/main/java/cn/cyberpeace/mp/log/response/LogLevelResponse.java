package cn.cyberpeace.mp.log.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogLevelResponse {

    @ApiModelProperty(value = "日志模块名")
    @NotBlank
    private String logName;

    @ApiModelProperty(value = "日志级别")
    private String logLevel;

}
