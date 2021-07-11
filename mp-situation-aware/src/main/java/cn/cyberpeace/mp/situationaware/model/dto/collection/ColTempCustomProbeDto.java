package cn.cyberpeace.mp.situationaware.model.dto.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
@Deprecated
public class ColTempCustomProbeDto extends ColTempCommonProbeDto {

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "类型 0:信息类 1:事件类 2:日志类")
    private Integer probeCategory;

    
    @ApiModelProperty(value = "(可多选)操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private String osType;

    
    @ApiModelProperty(value = "脚本参数")
    private String parameter;

    
    @JsonIgnore
    private List<String> paths;

    
    
    

    
    @ApiModelProperty(value = "探针脚本内容fdfs地址(采集模板单独存一份，防止自定义探针代码修改之后，影响到采集模板)")
    private String scriptUrl;

}
