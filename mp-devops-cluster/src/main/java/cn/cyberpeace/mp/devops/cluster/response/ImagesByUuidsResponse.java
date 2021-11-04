package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据ID查询返回信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "根据UUID集合查询返回信息")
public class ImagesByUuidsResponse  {

    /**
     * UUID
     */
    @ApiModelProperty(value = "UUID")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

}
