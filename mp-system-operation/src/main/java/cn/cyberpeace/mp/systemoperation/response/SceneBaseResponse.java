package cn.cyberpeace.mp.systemoperation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 场景通用返回体
 * @Autho qiaokang
 * @Date：2021/7/9 10:22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("节点监控返回")
public class SceneBaseResponse<T> {

    @ApiModelProperty("关联关系")
    private Links links;

    @ApiModelProperty("数据")
    private List<T> data;

}
