package cn.cyberpeace.mp.cscene.dto.logfile;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LogFileBasicDto {

    @ApiModelProperty(value = "节点id")
    private String elementId;

    @ApiModelProperty(value = "探针名称")
    private String agentName;

    @ApiModelProperty(value = "系统类型")
    private String osType;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("文件名")
    private String name;

}
