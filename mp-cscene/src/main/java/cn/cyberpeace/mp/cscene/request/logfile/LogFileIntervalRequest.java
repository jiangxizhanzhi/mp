package cn.cyberpeace.mp.cscene.request.logfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/7 10:04
 */
@Data
public class LogFileIntervalRequest {

    @ApiModelProperty(value = "间隔时间")
    private Integer Interval;

}
