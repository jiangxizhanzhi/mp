 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionTemplatePageItemDto.java.
 // File size 665.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/25 09:40
 */
@Data
@ApiModel("采集模板分页项")
public class CollectionTemplatePageItemDto extends CollectionTemplateModel {

    @ApiModelProperty(value = "探针个数")
    private Integer probeCounts;
    /**
     * 创建者用户名
     */
    @ApiModelProperty(value = "创建者姓名")
    private String creatorName;
}
