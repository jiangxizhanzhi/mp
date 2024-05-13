package cn.cyberpeace.mp.cscene.request.ojj;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 根据ids获取载荷
 */
@Data
@ApiModel("根据ids获取载荷")
public class IdsRequest {

    /**
     * 截屏文件id集合
     */
    @ApiModelProperty("载荷ids集合")
    @NotNull(message = "ids can not be empty")
    private List<String> ids;

}
