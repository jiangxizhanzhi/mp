package cn.cyberpeace.mp.devops.cluster.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtDocker{

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String file;

    /**
     * 启动参数
     */
    @ApiModelProperty(value = "启动参数",hidden = true)
    private List<StartUpParameter> settings;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer syncStatus;

    /**
     * 临时文件ID
     */
    @ApiModelProperty("临时文件ID")
    private String tmpdirUuid;


    @ApiModelProperty("用户文件名称")
    private String userFilename;
}
