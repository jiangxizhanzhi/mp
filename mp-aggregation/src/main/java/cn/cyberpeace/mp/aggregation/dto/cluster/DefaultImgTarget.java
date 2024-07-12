 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name DefaultImgTarget.java.
 // File size 778.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName DefaultImgTarget
 * @Description
 * @Author guquan
 * @Date 2023/9/2 11:22
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("靶标图集响应")
public class DefaultImgTarget {

    @ApiModelProperty(value = "图集", notes = "图集")
    List<String> imgs;

    @ApiModelProperty(value = "风格", notes = "风格")
    String style;

    @ApiModelProperty(value = "名称", notes = "名称")
    String name;
}
