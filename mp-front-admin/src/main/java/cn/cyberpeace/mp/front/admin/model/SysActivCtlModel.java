package cn.cyberpeace.mp.front.admin.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_front_admin_activ_ctl")
public class SysActivCtlModel extends BaseModel {

    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "随机盐")
    @DefaultStr
    private String salt;

    @ApiModelProperty(value = "系统当前时间戳，用于防止服务器时间回拨，以延长激活使用时间")
    private Long sysTimestamp;

    @ApiModelProperty(value = "激活时间戳，用于每次生成sys-uuid")
    private Long activeTimestamp;

}
