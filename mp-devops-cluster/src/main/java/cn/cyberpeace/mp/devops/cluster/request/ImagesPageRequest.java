package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 镜像分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "镜像分页请求信息")
public class ImagesPageRequest extends BasePageRequest {

    /**
     * 角色
     */
    private Integer role;

    /**
     * 系统类型
     */
    private Integer systemType;

    /**
     * 镜像类型
     */
    private Integer imageType;

    /**
     * 镜像名称
     */
    private String name;
}
