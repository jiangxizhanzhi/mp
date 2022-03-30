package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 镜像同步请求信息
 */
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
