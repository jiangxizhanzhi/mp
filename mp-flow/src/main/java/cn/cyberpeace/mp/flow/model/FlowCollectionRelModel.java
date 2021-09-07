package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 流量集合关联对象
 * @Author qiaokang
 * @Date：2021/8/31 10:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_collection_rel")
public class FlowCollectionRelModel extends BaseModel {

    /**
     * 流量集合id
     */
    @ApiModelProperty(value = "流量集合id")
    @NotBlank(message = "collectionId can not be empty")
    private String collectionId;

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    @NotBlank(message = "flowId can not be empty")
    private String flowId;

}
