package cn.cyberpeace.mp.frontadmin.dto.notice;

import cn.cyberpeace.mp.auth.bo.user.UserSimpleBo;
import cn.cyberpeace.mp.base.model.stereotype.DefaultLong;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SysNoticeDto {

    @ApiModelProperty(value = "租户")
    private String id;

    @ApiModelProperty(value = "租户")
    private String tenant;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "发布人 (id)")
    private String publisher;

    @ApiModelProperty(value = "发布人 (用户名)")
    private String publisherUserName;

    
    @ApiModelProperty(value = "1:系统  2:课程  3:练习  4:考试")
    private Integer type;

    @ApiModelProperty(value = "标签，多个用逗号隔开")
    private String tags;

    @ApiModelProperty(value = "组织id集合")
    List<String> orgIds;

    @ApiModelProperty(value = "用户简单信息集合")
    List<UserSimpleBo> users;

    @ApiModelProperty(value = "组织，多个用逗号隔开")
    private String orgPaths;

    @ApiModelProperty(value = "置顶数字(大于0表示置顶)")
    @DefaultLong(0)
    private Long top;

    @ApiModelProperty(value = "是否公开，对所有人可见  0:否 1:是 默认是0  (注意，如果传了1，则后台会忽略roleIds的内容)")
    private Integer isPublic;

    @ApiModelProperty(value = "角色id集合")
    List<String> roleIds;

    @ApiModelProperty(value = "角色，多个用逗号隔开")
    private String roleNames;

    @ApiModelProperty(value = "是否已读")
    private Integer isRead;

    @ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
