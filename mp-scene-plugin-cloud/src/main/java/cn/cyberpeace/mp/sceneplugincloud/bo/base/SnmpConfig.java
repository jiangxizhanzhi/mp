package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
public class SnmpConfig implements Serializable {

    private static final long serialVersionUID = 389638345766063602L;

    @ApiModelProperty(value = "开关")
    private Boolean configSwitch;

    @ApiModelProperty(value = "snmp版本")
    private String version;
    
    @ApiModelProperty(value = "团体字")
    private String communityName;
    
    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "场景设备ip")
    private String sceneIp;

    @ApiModelProperty(value = "场景设备端口")
    private Integer scenePort;
    
    @ApiModelProperty(value = "v3用户名")
    private String username;
    @ApiModelProperty(value = "v3权限key")
    private String authKey;
    @ApiModelProperty(value = "v3个人key")
    private String privKey;

    private FileUploadInfo fileInfo;
}
