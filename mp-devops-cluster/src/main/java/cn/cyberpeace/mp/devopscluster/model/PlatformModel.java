package cn.cyberpeace.mp.devopscluster.model;

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
 * @Description 超融合平台管理对象
 * @Author guquan
 * @Date 2025-09-08 16:17:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("mp_platform")
public class PlatformModel extends BaseModel {

    /**
     * 告警信息
     */
    @ApiModelProperty(value = "平台名称")
    @DefaultStr
    private String name;

    /**
     * 告警信息
     */
    @ApiModelProperty(value = "厂商")
    @DefaultStr
    private String company;

    /**
     * 告警信息
     */
    @ApiModelProperty(value = "参数")
    @DefaultStr
    private String params;

    /**
     * 告警信息
     */
    @ApiModelProperty(value = "架构类型")
    @DefaultStr
    private String systemType;

    /**
     * 远程连接地址
     */
    @ApiModelProperty(value = "远程连接地址")
    @DefaultStr
    private String url;

    /**
     * 告警信息
     */
    @ApiModelProperty(value = "在位状态：0 否，1 是")
    @DefaultInt
    private Integer inPosition;

    @ApiModelProperty(value = "接入信息")
    @DefaultStr
    private String accessProtocols;

    /**
     * 告警信息
     */
    @ApiModelProperty(value = "创建用户id")
    @DefaultStr
    private String createUser;

    /**
     * 告警信息
     */
    @ApiModelProperty(value = "创建用户名称")
    @DefaultStr
    private String createUsername;

}
