package cn.cyberpeace.mp.aggregation.dto.dashboard;

import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/26 11:04
 */
@Data
public class UserOnlineReportMonitorBo extends UserOnlineReportBo {

    @ApiModelProperty(value = "是否有会话信息")
    private boolean hasNodeSession = false;

    @ApiModelProperty(value = "会话数量")
    private int nodeSessionCnt = 0;

}
