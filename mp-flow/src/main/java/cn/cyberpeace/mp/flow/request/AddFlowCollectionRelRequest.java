package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增流量集合关联请求")
public class AddFlowCollectionRelRequest {

    
    @ApiModelProperty(value = "集合id")
    @NotBlank(message = "collectionId can not be empty")
    private String collectionId;

    
    @ApiModelProperty(value = "流量id集合")
    @NotEmpty(message = "flowIds can not be empty")
    private List<String> flowIds;

}
