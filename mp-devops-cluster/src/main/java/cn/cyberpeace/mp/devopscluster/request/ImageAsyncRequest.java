package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageAsyncRequest extends BaseRequest {

    @ApiModelProperty("镜像ID集合")
    private List<Integer> ids;

    @ApiModelProperty("是否隐藏")
    private Boolean isHide = Boolean.FALSE;
}
