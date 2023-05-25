package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.bo.base
 * @Author: hhuang
 * @CreateTime: 2023/05/18  17:34
 * @Description: snmp配置信息
 * @Version: 1.0
 */
@Data
public class SnmpConfig implements Serializable {

    private static final long serialVersionUID = 389638345766063602L;

    @ApiModelProperty(value = "开关")
    private Boolean configSwitch;

    @ApiModelProperty(value = "snmp版本")
    private String version;
    //团体字，一般为public
    @ApiModelProperty(value = "团体字")
    private String communityName;
    //ip地址
    @ApiModelProperty(value = "ip地址")
    private String ip;
    /**
     * snmp版本为v3时，会出现以下三个参数
     *  用户名
     */
    @ApiModelProperty(value = "v3用户名")
    private String username;
    @ApiModelProperty(value = "v3权限key")
    private String authKey;
    @ApiModelProperty(value = "v3个人key")
    private String privKey;

    private FileUploadInfo fileInfo;
}
