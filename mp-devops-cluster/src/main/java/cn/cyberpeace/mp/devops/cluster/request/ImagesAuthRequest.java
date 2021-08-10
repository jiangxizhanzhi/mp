package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
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
@ApiModel(value = "镜像授权请求信息")
public class ImagesAuthRequest extends BaseRequest {

    /**
     * 镜像ID
     */
    private String imageId;

    /**
     * 用户ID
     */
    private List<String> userIds;

}
