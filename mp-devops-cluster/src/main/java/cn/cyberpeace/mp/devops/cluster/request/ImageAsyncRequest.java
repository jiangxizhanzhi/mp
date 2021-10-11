package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
    @NotBlank
    private List<Integer> ids;
}
