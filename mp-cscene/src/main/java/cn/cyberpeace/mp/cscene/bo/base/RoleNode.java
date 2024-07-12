 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RoleNode.java.
 // File size 405.
 // File create date Fri Jul 12 12:55:32 CST 2024.
 // File update date Fri Jul 12 12:55:32 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.cscene.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName RoleNode
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class RoleNode {
    @ApiModelProperty(value = "节点id")
    private String id;
    @ApiModelProperty(value = "分类类型")
    private String category;
}
