package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统公告消息(已读/删除)统计表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_sys_notice_read")
public class SysNoticeReadModel extends BaseModel {

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "公告ID")
    private String noticeId;

    // 读者
    @ApiModelProperty(value = "读者")
    private String user;

    @ApiModelProperty(value = "是否已读 1:已读 0:未读 默认为1  首次创建一条数据即为已读")
    @DefaultInt(0)
    private Integer isReaded;

    @ApiModelProperty(value = "是否推送本条公告 1:是（需要推送）； 0:否（不再推送） 默认为1")
    @DefaultInt(1)
    private Integer isNeedPush;

    // 阅读次数
    @ApiModelProperty(value = "阅读次数")
    @DefaultInt(0)
    private Integer readCount;
}
