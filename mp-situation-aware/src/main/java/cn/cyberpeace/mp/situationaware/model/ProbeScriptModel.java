package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 探针脚本 实体， 跟探针一对一关系
 * mp_situation_probe_script
 * @author 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_probe_script")
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
    @DefaultInt(1)
    private Integer version;

    /**
     * 脚本状态
     */
    @ApiModelProperty(value = "脚本状态")
    @DefaultInt(1)
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
