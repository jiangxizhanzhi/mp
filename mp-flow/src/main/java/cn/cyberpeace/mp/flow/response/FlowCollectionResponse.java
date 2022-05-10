package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author qiaokang
 * @Date：2022/5/10 10:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量集合返回信息")
public class FlowCollectionResponse extends FlowCollectionModel {

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量，3 流量集合
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量，3 流量集合")
    private Integer interactiveType = 3;

    /**
     * 描述(兼容前端修改)
     */
    @ApiModelProperty(value = "描述")
    private String introduction;

}
