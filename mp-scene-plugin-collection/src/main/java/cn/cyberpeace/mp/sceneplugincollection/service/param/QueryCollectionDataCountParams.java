package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class QueryCollectionDataCountParams {

    @ApiModelProperty(value = "节点id列表")
    private String sceneId;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "节点id列表")
    private List<String> nodeIds;

}
