package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.controller.request
 * @Author: hhuang
 * @CreateTime: 2023/05/12  18:03
 * @Description: mib节点信息查询请求
 * @Version: 1.0
 */
@Data
public class MibInfoQueryRequest {
    @ApiModelProperty(value = "oid")
    @NotNull(message = "oid can not be empty")
    private String oid;
    //snmp版本
    @ApiModelProperty(value = "snmp版本")
    @NotNull(message = "snmp version can not be empty")
    private String version;
    //团体字，一般为public
    @ApiModelProperty(value = "团体字")
    @NotNull(message = "community can not be empty")
    private String community;
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
}
