package cn.cyberpeace.mp.aggregation.dto.dashboard;

import cn.cyberpeace.mp.auth.dto.user.UserOnlineReportDto;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/8/26 11:04
 */
@Data
public class UserOnlineReportMonitorDto extends UserOnlineReportDto {

    private boolean hasNodeSession = false;

    private int nodeSessionCnt = 0;

}
