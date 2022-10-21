package cn.cyberpeace.mp.auth.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/10/25 17:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOnlineRecordSummaryDto {

    @ApiModelProperty(value = "用户id")
    private String user;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "累计在线时长（分钟）")
    private Long totalOnlineMinutes;

    @ApiModelProperty(value = "未完成状态数量")
    private Integer unCompleteCnt;
}
