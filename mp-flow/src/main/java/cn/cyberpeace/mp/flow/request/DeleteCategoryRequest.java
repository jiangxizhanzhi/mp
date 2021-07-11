package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "删除流量分类请求")
public class DeleteCategoryRequest {

    @NotEmpty
    @ApiModelProperty(value = "id列表")
    private List<String> ids;
}
