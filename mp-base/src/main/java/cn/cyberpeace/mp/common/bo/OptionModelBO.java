 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OptionModelBO.java.
 // File size 540.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.common.bo;

import cn.cyberpeace.mp.common.model.OptionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName OptionModelBO
 * @Description 选项
 * @Author guquan
 * @Date 2023/6/6 9:30
 * @Version 1.0.0
 * @see OptionModel
 */
@Data
@ApiModel(value = "选项")
public class OptionModelBO extends OptionModel {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
}
