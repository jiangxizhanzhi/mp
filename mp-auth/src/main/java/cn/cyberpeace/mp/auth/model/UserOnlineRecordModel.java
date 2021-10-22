package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@NoArgsConstructor
@Table("mp_auth_user_online_record")
public class UserOnlineRecordModel extends BaseModel {

    public static final Integer STATUS_FINISHED = 1;

    public static final Integer STATUS_NOT_FINISHED = 0;

    @ApiModelProperty(value = "用户id")
    private String user;

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "组织")
    private String org;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "累计在线分钟数")
    private Integer durationMinutes;

    @ApiModelProperty("记录完成与否 0:未完成 1:已完成")
    private Integer status;

    @Builder
    public UserOnlineRecordModel(String id, LocalDateTime createTime, LocalDateTime updateTime, String user, String tenant, String username, String name, String org, LocalDateTime startTime, LocalDateTime endTime, Integer durationMinutes, Integer status) {
        super(id, createTime, updateTime);
        this.user = user;
        this.tenant = tenant;
        this.username = username;
        this.name = name;
        this.org = org;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }
}
