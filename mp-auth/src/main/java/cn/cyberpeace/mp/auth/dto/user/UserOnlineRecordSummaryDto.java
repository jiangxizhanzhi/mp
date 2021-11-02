package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/25 17:37
 */
@Data
@Builder
public class UserOnlineRecordSummaryDto {

    @ApiModelProperty(value = "用户id")
    private String user;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "累计在线时长（小时）")
    private Double totalOnlineHours;

}
