 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EntityNodeMibFileResult.java.
 // File size 779.
 // File create date Fri Jul 12 12:55:25 CST 2024.
 // File update date Fri Jul 12 12:55:25 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EntityNodeMibFileResult
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
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
