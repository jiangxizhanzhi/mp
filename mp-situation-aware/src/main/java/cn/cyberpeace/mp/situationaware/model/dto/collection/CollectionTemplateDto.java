 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CollectionTemplateDto.java.
 // File size 921.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.model.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/25 09:40
 */
@Data
@ApiModel(value = "采集模板")
public class CollectionTemplateDto extends CollectionTemplatePageItemDto {

    @ApiModelProperty(value = "基础信息探针列表")
    private List<ColTempCommonProbeDto> basicInfoProbes;

    @ApiModelProperty(value = "事件采集探针列表")
    private List<ColTempCommonProbeDto> eventProbes;

    @ApiModelProperty(value = "日志采集探针列表")
    private List<ColTempCommonProbeDto> logProbes;

    // 返回参数比通用探针要多一些
    @ApiModelProperty(value = "自定义探针列表")
    private List<CollectionTemplateProbeDto> customProbes;

}
