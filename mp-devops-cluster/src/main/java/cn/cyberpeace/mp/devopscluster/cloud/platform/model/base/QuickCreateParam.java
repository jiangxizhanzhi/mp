package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuickCreateParam {
    @ApiModelProperty(value = "用户uid")
    private String installerUuid;
    @ApiModelProperty(value = "快速构建启动参数")
    private List<StartUpParameter> settings;
    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "头像")
    private String logo;
    @ApiModelProperty(value = "file")
    private String file;
}
