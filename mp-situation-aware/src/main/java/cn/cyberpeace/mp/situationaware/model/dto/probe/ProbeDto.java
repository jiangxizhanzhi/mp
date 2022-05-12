package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.ProbeModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/26 10:06
 */
@Data
public class ProbeDto extends ProbeModel {

    @ApiModelProperty(value = "探针脚本类型 0:shell 1:python 2:bat")
    private Integer scriptType;

    /**
     * 探针脚本内容
     */
    @ApiModelProperty(value = "探针脚本内容")
    // todo blob -> string?
    private String scriptContent;

    /**
     * 探针脚本内容fdfs地址
     */
    @ApiModelProperty(value = "探针脚本内容fdfs地址")
    private String scriptUrl;


    @ApiModelProperty(value = "集合类型，操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private List<Integer> osTypes;

    /**
     * 脚本参数(JSON)
     */
    @ApiModelProperty(value = "脚本参数")
    private String parameter;

}
