package cn.cyberpeace.mp.auth.dto.notice;

import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.dto.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 公告请求信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SysNoticeRequest {

    @ApiModelProperty(value = "公告ID")
    @NotNull(groups = {UpdateGroup.class}, message = "ID不能为空！")
    private String id;

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

    // 1:系统  2:课程  3:练习  4:考试
    @ApiModelProperty(value = "1:系统  2:课程  3:练习  4:考试")
    private Integer type;

    @ApiModelProperty(value = "角色id列表")
    private List<String> roleIds;

    @ApiModelProperty(value = "组织id列表")
    private List<String> orgIds;

}
