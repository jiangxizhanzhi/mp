package cn.cyberpeace.mp.agent.snmp.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SnmpAccessInfo extends BasicAccessInfo {

    @ApiModelProperty("版本")
    private String version;

    @ApiModelProperty("共同体名称")
    private String community;




    /*****************************************V3****************************************/
    @ApiModelProperty("V3安全等级")
    private Integer secLevel;

    @ApiModelProperty("V3用户")
    private String username;

    @ApiModelProperty("V3认证算法")
    private String authProtocol;

    @ApiModelProperty("V3认证密码")
    private String authPassPhrase;

    @ApiModelProperty("V3加密算法")
    private String privacyProtocol;

    @ApiModelProperty("V3加密密码")
    private String privacyPassPhrase;

}
