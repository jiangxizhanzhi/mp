package cn.cyberpeace.mp.frontadmin.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultLong;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sys_notice")
@ApiModel
public class SysNoticeModel extends BaseModel {

    public static final int NOTICE_PUBLIC = 1;

    public static final int NOTICE_NOT_PUBLIC = 0;

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "标题")
    @NotBlank
    private String title;

    @ApiModelProperty(value = "发布人")
    private String publisher;

    
    @ApiModelProperty(value = "1:系统  2:课程  3:练习  4:考试")
    @DefaultInt(1)
    private Integer type;

    @ApiModelProperty(value = "置顶数字(采用当前时间戳，越大越靠前)")
    @DefaultLong(0)
    private Long top;

    @ApiModelProperty(value = "是否公开，对所有人可见  0:否 1:是 默认是0  (注意，如果传了1，则后台会忽略roleIds的内容)")
    @DefaultInt(0)
    private Integer isPublic;

}
