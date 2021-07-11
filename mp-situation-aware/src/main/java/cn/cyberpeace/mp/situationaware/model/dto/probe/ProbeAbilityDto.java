package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.ProbeAbilityModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class ProbeAbilityDto extends ProbeAbilityModel {

    
    @ApiModelProperty(value = "探针id")
    private String probeId;

    
    @ApiModelProperty(value = "探针名称")
    private String probeName;

    @ApiModelProperty(value = "分析引擎")
    private String engine;


    @ApiModelProperty(value = "集合类型，操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other, 5:cisco-ios, 6:HP, 7:junos, 8:openbsd")
    private List<Integer> osTypes;


}
