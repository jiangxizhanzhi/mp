package cn.cyberpeace.mp.devopscluster.request;

import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesAuthRequest extends BaseRequest {

    
    @ApiModelProperty("镜像UUID集合")
    @NotEmpty
    private List<String> uuids;

    
    @ApiModelProperty("用户ID集合")
    private List<String> userIds;

    
    @ApiModelProperty("是否公开")
    private PublicEnum isAllPublic;
}
