 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EntityUsb.java.
 // File size 628.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName EntityUsb
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@ApiModel("实体USB对象")
public class EntityUsb {

    /**
     * UUID
     */
    @ApiModelProperty("UUID")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 主机名
     */
    @ApiModelProperty("主机名")
    private String host;
}
