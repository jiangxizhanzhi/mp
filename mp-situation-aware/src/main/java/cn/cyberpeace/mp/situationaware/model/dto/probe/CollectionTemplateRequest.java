package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/26 16:26
 */
@Data
@ApiModel("采集模板请求体")
public class CollectionTemplateRequest extends CollectionTemplateModel {

    @ApiModelProperty(value = "基础信息探针列表")
    private List<CollectionTemplateProbeDto> basicInfoProbes;

    @ApiModelProperty(value = "事件采集探针列表")
    private List<CollectionTemplateProbeDto> eventProbes;

    @ApiModelProperty(value = "日志采集探针列表")
    private List<CollectionTemplateProbeDto> logProbes;

    @ApiModelProperty(value = "自定义探针列表")
    private List<CollectionTemplateProbeDto> customProbes;

}
