package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName ListByCollectionIdsRequest
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "根据集合id批量查询请求")
public class ListByCollectionIdsRequest {

    /**
     * 集合id
     */
    @ApiModelProperty(value = "集合id")
    @NotNull(message = "collectionIds can not be empty")
    private List<String> collectionIds;

}
