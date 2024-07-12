 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TypeConvertModel.java.
 // File size 711.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName TypeConvertModel
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class TypeConvertModel {

    /**
     * 类型ID
     */
    @ApiModelProperty("类型ID")
    private Integer id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 2级类型ID
     */
    @ApiModelProperty("2级类型ID")
    private Integer l2ParentId;

    /**
     * 1级类型ID
     */
    @ApiModelProperty("1级类型ID")
    private Integer l1ParentId;

}
