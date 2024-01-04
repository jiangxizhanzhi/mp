package cn.cyberpeace.mp.cscene.request.logfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/7 10:04
 */
@Data
public class LogFileIntervalRequest {

    @NotEmpty
    @ApiModelProperty(value = "间隔时间", required = true)
    private Integer Interval;

}
