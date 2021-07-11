package cn.cyberpeace.mp.auth.bo.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOnlineRecordSummaryBo {

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
