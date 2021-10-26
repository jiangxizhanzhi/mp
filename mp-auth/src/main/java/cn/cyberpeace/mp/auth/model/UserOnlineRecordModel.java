package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.generator.DateTimeGenerator;
import cn.cyberpeace.mp.base.model.stereotype.DefaultGen;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
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

    @ApiModelProperty(value = "开始时间")
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime startTime;

    @ApiModelProperty(value = "结束时间")
    @DefaultGen(DateTimeGenerator.class)
    private LocalDateTime endTime;

    @ApiModelProperty(value = "累计在线分钟数")
    @DefaultInt(0)
    private Integer durationMinutes;

    @ApiModelProperty("记录完成与否 0:未完成 1:已完成")
    @DefaultInt(0)
    private Integer status;

    @Builder
    public UserOnlineRecordModel(String id, LocalDateTime createTime, LocalDateTime updateTime, String user, String tenant, String username, String name, LocalDateTime startTime, LocalDateTime endTime, Integer durationMinutes, Integer status) {
        super(id, createTime, updateTime);
        this.user = user;
        this.tenant = tenant;
        this.username = username;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.status = status;
    }
}
