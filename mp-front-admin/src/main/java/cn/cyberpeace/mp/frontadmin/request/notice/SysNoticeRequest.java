package cn.cyberpeace.mp.frontadmin.request.notice;

import cn.cyberpeace.mp.frontadmin.request.validate.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SysNoticeRequest {

    @ApiModelProperty(value = "租户")
    private String tenant;

    @ApiModelProperty(value = "内容")
    @NotBlank(groups = AddGroup.class, message = "内容不能为空！")
    private String content;

    @ApiModelProperty(value = "标题")
    @NotBlank(groups = AddGroup.class, message = "标题不能为空！")
    private String title;

    @ApiModelProperty(value = "发布人")
    private String publisher;

    @ApiModelProperty(value = "标签")
    private List<String> tags;

    
    @ApiModelProperty(value = "1:系统  2:课程  3:练习  4:考试")
    private Integer type;

    @ApiModelProperty(value = "是否公开，对所有人可见  0:否 1:是 默认是0  (注意，如果传了1，则后台会忽略roleIds的内容)")
    private Integer isPublic;

    @ApiModelProperty(value = "角色id列表")
    private List<String> roleIds;

    @ApiModelProperty(value = "组织id列表")
    private List<String> orgIds;

    @ApiModelProperty(value = "用户id列表")
    private List<String> userIds;

}
