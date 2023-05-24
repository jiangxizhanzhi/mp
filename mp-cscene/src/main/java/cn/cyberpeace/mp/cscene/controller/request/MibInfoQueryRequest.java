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

    @ApiModelProperty(value = "场景id")
    @NotNull(message = "sceneId can not be empty")
    private String sceneId;

    @ApiModelProperty(value = "节点id")
    @NotNull(message = "elementId can not be empty")
    private String elementId;

    @ApiModelProperty(value = "oid")
    @NotNull(message = "oid can not be empty")
    private String oid;
    //snmp版本
    @ApiModelProperty(value = "snmp版本")
    private String version;
    //团体字，一般为public
    @ApiModelProperty(value = "团体字")
    private String community;
    /**
     * 宿主机ip地址
     */
    private String ip;
    /**
     * 宿主机snmp协议转换端口
     */
    private String port;
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
