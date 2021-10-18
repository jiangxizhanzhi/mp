package cn.cyberpeace.mp.devops.cluster.converter.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件注入
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel("文件注入模型")
public class InjectFileConvertModel extends BasePageConvertModel {

    @ApiModelProperty("UUID")
    private String injectUuid;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("路径")
    private String destPath;

    @ApiModelProperty("大小")
    private Integer size;

    @ApiModelProperty("状态")
    private String status;
}
