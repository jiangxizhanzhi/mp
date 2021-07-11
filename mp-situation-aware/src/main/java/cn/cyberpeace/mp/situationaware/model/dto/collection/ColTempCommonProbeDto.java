package cn.cyberpeace.mp.situationaware.model.dto.collection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class ColTempCommonProbeDto {

    
    @ApiModelProperty(value = "探针ID")
    @NotBlank
    private String id;

    
    @ApiModelProperty(value = "探针名称")
    private String name;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    


    @ApiModelProperty(value = "0:信息类 1:事件类 2:日志类")
    private Integer probeCategory;

    @ApiModelProperty(value = "0:通用探针 1:自定义探针")
    private Integer type;

    
    @ApiModelProperty(value = "0:启用，1:禁用")
    @NotNull
    private Integer disabled;

    
    @ApiModelProperty(value = "探针配置，路径数组 如：[\"/var/log/mp.log\", \"/etc/bin\"] 或者注册表选项 [\"HKEY_LOCAL_MACHINE\\SYSTEM\\Setup\"]")
    private List<String> paths;

}
