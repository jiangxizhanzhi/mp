package cn.cyberpeace.mp.aggregation.dto.dashboard;

import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户在线报表监控bo
 *
 * @author 二竹
 * @date 2025/01/17
 */
@Data
public class UserOnlineReportMonitorBo extends UserOnlineReportBo {

    @ApiModelProperty(value = "是否有会话信息")
    private boolean hasNodeSession = false;

    @ApiModelProperty(value = "会话数量")
    private int nodeSessionCnt = 0;

}
