package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.dto.common.PageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class UserOnlineRecordSummaryPageRequest extends PageRequest {

    @ApiModelProperty(value = "userId")
    private String userId;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

}
