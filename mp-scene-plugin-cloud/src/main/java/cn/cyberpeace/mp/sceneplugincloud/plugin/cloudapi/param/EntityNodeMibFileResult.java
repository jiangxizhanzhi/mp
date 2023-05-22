package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param
 * @Author: hhuang
 * @CreateTime: 2023/05/15  15:31
 * @Description: 实装snmp mib文件信息解析响应
 * @Version: 1.0
 */
@Data
public class EntityNodeMibFileResult {

    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "oid")
    private String oid;
    @ApiModelProperty(value = "被管理对象类型")
    private String syntax;
    @ApiModelProperty(value = "节点支持的访问方式")
    private String maxAccess;
    @ApiModelProperty(value = "节点的状态")
    private String status;
    @ApiModelProperty(value = "父层级oid")
    private String parent;

}
