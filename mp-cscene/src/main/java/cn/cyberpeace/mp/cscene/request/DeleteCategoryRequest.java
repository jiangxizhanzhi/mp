package cn.cyberpeace.mp.cscene.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @Description: 删除场景实例库
 * @Author: guquan
 * @Date：2023/5/6 10:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "删除场景实例库请求")
public class DeleteCategoryRequest {

    @NotEmpty
    @ApiModelProperty(value = "id列表")
    private List<String> ids;
}
