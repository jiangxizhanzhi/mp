 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name InjectFileConvertModel.java.
 // File size 876.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName InjectFileConvertModel
 * @Description 文件注入
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageConvertModel
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("文件注入模型")
public class InjectFileConvertModel extends BasePageConvertModel {

    @ApiModelProperty("UUID")
    private String injectUuid;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("路径")
    private String destPath;

    @ApiModelProperty("大小")
    private Integer size;

    @ApiModelProperty("状态")
    private String status;
}
