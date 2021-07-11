package cn.cyberpeace.mp.frontadmin.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultLong;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_front_admin_activ_ctl")
public class SysActivCtlModel extends BaseModel {

    
    public static final Integer STATUS_INVALID = 0;

    
    public static final Integer STATUS_VALID = 1;

    
    public static final Integer STATUS_UNUSED = 2;


    @ApiModelProperty(value = "租户")
    @DefaultStr(AuthConstant.DEFAULT_TENANT)
    private String tenant;

    @ApiModelProperty(value = "随机盐")
    @DefaultStr
    private String salt;

    @ApiModelProperty(value = "系统UUID后缀，6位随机数字+字母，当前表中每次新增一条数据，则随机产生一个suffix")
    @DefaultStr
    private String suffix;

    @ApiModelProperty(value = "（密文）激活码")
    @DefaultStr
    private String licenseCode;

    @ApiModelProperty(value = "（密文）系统首次激活日期。如果同一个license_code多次激活，最终的过期日期以第一次激活日期为准")
    @DefaultStr
    private String firstActiveDate;

    @ApiModelProperty(value = "（密文）系统最近一次激活日期，同一个激活码的情况下")
    @DefaultStr
    private String lastActiveDate;

    @ApiModelProperty(value = "（密文）过期日期，加密存放，实际判断逻辑为第二天的0时0分0秒")
    @DefaultStr
    private String expireDate;

    @ApiModelProperty(value = "同一个激活码的激活次数")
    @DefaultInt(1)
    private Integer activeCount;

    @ApiModelProperty(value = "定时任务记录的系统时间戳，发生时间回拨，则该值也回到回拨时间，用于累计系统运行秒数")
    @DefaultLong(-1)
    private Long sysTimestamp;

    @ApiModelProperty(value = "(密文）系统运行秒数(该条件与过期日期，二者达到其一即可判断为license过期。防止通过回拨服务器时间，达到白嫖目的)")
    @DefaultStr
    private String sysRunSeconds;

    @ApiModelProperty(value = "（密文）系统寿命，用于判断runSeconds是否超期")
    @DefaultStr
    private String sysLifetime;

    @ApiModelProperty(value = "状态 2:生成的随机码，未使用，1:有效，0:无效")
    @DefaultInt(1)
    private Integer status;


}
