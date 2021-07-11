package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@ToString
@Data
public class ProbeDto extends ProbeModel {

    @ApiModelProperty(value = "探针脚本类型 0:shell 1:python 2:bat")
    private Integer scriptType;

    
    @ApiModelProperty(value = "探针脚本内容")
    
    private String scriptContent;

    
    @ApiModelProperty(value = "探针脚本内容fdfs地址")
    private String scriptUrl;


    @ApiModelProperty(value = "集合类型，操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private List<Integer> osTypes;

    
    @ApiModelProperty(value = "脚本参数")
    private String parameter;

}
