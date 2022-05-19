package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.StartUpParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "服务分页返回信息")
public class InstallerPageResponse extends BaseCloudPlatformResponse{

    /**
     * UUID
     */
    @ApiModelProperty("UUID")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 图标地址
     */
    @ApiModelProperty("图标地址")
    private String logo;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 类型
     */
    @ApiModelProperty("类型")
    private String categories;

    /**
     * 服务类型：windows服务/docker服务
     */
    @ApiModelProperty("服务类型")
    private Integer deployType;

    /**
     * 启动参数（包装对象）
     */
    @ApiModelProperty("启动参数")
    private List<StartUpParameter> startUpParameterList;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String file;
}
