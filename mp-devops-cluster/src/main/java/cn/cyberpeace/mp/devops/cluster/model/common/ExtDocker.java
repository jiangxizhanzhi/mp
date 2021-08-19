package cn.cyberpeace.mp.devops.cluster.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtDocker{

    /**
     * ID
     */
    @ApiModelProperty(value = "ID",hidden = true)
    @JsonIgnore
    private Integer id;

    /**
     * UUID
     */
    @ApiModelProperty(value = "UUID",hidden = true)
    @JsonIgnore
    private String installerUuid;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String file;

    /**
     * 启动参数
     */
    @ApiModelProperty(value = "启动参数",hidden = true)
    private String settings;

    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    private Integer syncStatus;

}
