package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * mp_situation_probe_script
 * @author 
 */
@Data
public class ProbeScriptModel extends BaseModel implements Serializable {

    /**
     * 探针id
     */
    @ApiModelProperty(value = "探针id")
    private String probeId;

    /**
     * 0:shell 1:python 2:bat
     */
    @ApiModelProperty(value = "0:shell 1:python 2:bat")
    private Integer scriptType;

    /**
     * 脚本版本
     */
    @ApiModelProperty(value = "脚本版本")
    private Integer version;

    /**
     * 脚本状态
     */
    @ApiModelProperty(value = "脚本状态")
    private Integer status;

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

}
