package cn.cyberpeace.mp.situationaware.model.dto.collection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@ApiModel(value = "采集模板")
public class CollectionTemplateDto extends CollectionTemplatePageItemDto {

    @ApiModelProperty(value = "基础信息探针列表")
    private List<ColTempCommonProbeDto> basicInfoProbes;

    @ApiModelProperty(value = "事件采集探针列表")
    private List<ColTempCommonProbeDto> eventProbes;

    @ApiModelProperty(value = "日志采集探针列表")
    private List<ColTempCommonProbeDto> logProbes;

    
    @ApiModelProperty(value = "自定义探针列表")
    private List<CollectionTemplateProbeDto> customProbes;

}
