package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公告-群组表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_sys_notice_group")
public class SysNoticeGroupModel extends BaseModel {

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "公告ID")
    private String noticeId;

    // 组织或群组
    @ApiModelProperty(value = "组织或群组")
    private String owner;
}
