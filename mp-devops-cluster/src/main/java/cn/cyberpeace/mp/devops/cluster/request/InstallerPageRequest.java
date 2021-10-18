package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerPageRequest extends BasePageRequest {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类")
    private String categories;

    /**
     * 服务类型：windows服务/docker服务
     */
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

}
