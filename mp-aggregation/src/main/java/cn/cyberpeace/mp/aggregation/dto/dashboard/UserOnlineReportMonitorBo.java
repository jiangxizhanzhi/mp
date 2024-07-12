 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserOnlineReportMonitorBo.java.
 // File size 621.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.dashboard;

import cn.cyberpeace.mp.auth.bo.user.UserOnlineReportBo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName UserOnlineReportMonitorBo
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/8/26 11:04
 * @Version 1.0.0
 * @see UserOnlineReportBo
 */
@Data
public class UserOnlineReportMonitorBo extends UserOnlineReportBo {

    @ApiModelProperty(value = "是否有会话信息")
    private boolean hasNodeSession = false;

    @ApiModelProperty(value = "会话数量")
    private int nodeSessionCnt = 0;

}
