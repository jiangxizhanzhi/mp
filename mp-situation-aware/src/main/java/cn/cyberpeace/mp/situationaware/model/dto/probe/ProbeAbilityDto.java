package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.ProbeAbilityModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/29 10:06
 */
@Data
public class ProbeAbilityDto extends ProbeAbilityModel {

    /**
     * 探针id
     */
    @ApiModelProperty(value = "探针id")
    private String probeId;

    /**
     * 功能名称
     */
    @ApiModelProperty(value = "探针名称")
    private String probeName;

    @JsonIgnore
    private String osType;


    @ApiModelProperty(value = "集合类型，操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private List<Integer> osTypes;


}
