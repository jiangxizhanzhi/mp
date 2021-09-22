package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 镜像授权请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagesAuthRequest extends BaseRequest {

    /**
     * 镜像ID
     */
    @ApiModelProperty("镜像UUID集合")
    private List<String> uuids;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID集合")
    private List<String> userIds;

    /**
     * 是否公开
     */
    @ApiModelProperty("是否公开")
    private PublicEnum isAllPublic;
}
