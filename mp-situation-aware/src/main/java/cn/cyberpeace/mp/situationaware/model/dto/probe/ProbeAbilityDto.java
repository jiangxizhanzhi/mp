package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.ProbeAbilityModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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


}
