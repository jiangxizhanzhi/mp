package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
public class MibInfoQueryRequest implements Serializable {

    private static final long serialVersionUID = 2365480436323068459L;

    @ApiModelProperty(value = "场景id")
    @NotNull
    @NotEmpty(message = "sceneId can not be empty")
    private String sceneId;

    @ApiModelProperty(value = "节点id")
    @NotNull
    @NotEmpty(message = "elementId can not be empty")
    private String elementId;

    @ApiModelProperty(value = "oid")
    @NotNull
    @NotEmpty(message = "oid can not be empty")
    private String oid;
    
    @ApiModelProperty(value = "snmp版本 V1:1,V2C:2,V3:3")
    @NotNull
    private Integer version;
    
    @ApiModelProperty(value = "团体字")
    private String communityName;
    
    @ApiModelProperty(value = "v3用户名")
    private String username;
    @ApiModelProperty(value = "v3认证密码")
    private String authKey;
    @ApiModelProperty(value = "v3加密密码")
    private String privKey;
}
