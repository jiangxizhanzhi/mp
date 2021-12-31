package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 扩展服务信息
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtNormal{

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表",hidden = true)
    private String resources;
}
