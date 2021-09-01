package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description: 新增流量集合关联请求
 * @Author qiaokang
 * @Date：2021/8/23 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增流量集合关联请求")
public class AddFlowCollectionRelRequest {

    /**
     * 集合id
     */
    @ApiModelProperty(value = "集合id")
    @NotBlank(message = "collectionId can not be empty")
    private String collectionId;

    /**
     * 流量id集合
     */
    @ApiModelProperty(value = "流量id集合")
    private List<String> flowIds;

}
