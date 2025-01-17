 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OptionModel.java.
 // File size 1325.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:15 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.common.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @author: shgang
 * @Date: 2022/7/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_common_option")
@ApiModel(value = "选项")
public class OptionModel extends BaseModel {

    @ApiModelProperty(value = "选项编码")
    @DefaultStr
    private String code;

    @ApiModelProperty(value = "中文名称")
    @DefaultStr
    private String zhName;

    @ApiModelProperty(value = "英文名称")
    @DefaultStr
    private String enName;

    @ApiModelProperty(value = "排序字段")
    @DefaultInt
    private Integer sort;

    @ApiModelProperty(value = "选项类型")
    @DefaultStr
    private String type;

    @ApiModelProperty(value = "父级选项编码")
    @DefaultStr
    private String parentCode;

    @ApiModelProperty(value = "元数据")
    @DefaultStr
    private String meta;
}

