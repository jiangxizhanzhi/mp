package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量集合返回信息")
public class FlowCollectionResponse extends FlowCollectionModel {

    
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量，3 流量集合")
    private Integer interactiveType = 3;

    
    @ApiModelProperty(value = "描述")
    private String introduction;

    
    @ApiModelProperty(value = "流量信息")
    private List<FlowInfoModel> flowInfoList;

}
