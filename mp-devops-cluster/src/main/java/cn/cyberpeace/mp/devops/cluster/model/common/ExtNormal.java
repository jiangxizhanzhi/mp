package cn.cyberpeace.mp.devops.cluster.model.common;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtNormal{

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
    private String uuid;

    /**
     * 服务ID
     */
    @ApiModelProperty(value = "服务ID",hidden = true)
    @JsonIgnore
    private String installerUuid;

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表",hidden = true)
    private String resources;
}
