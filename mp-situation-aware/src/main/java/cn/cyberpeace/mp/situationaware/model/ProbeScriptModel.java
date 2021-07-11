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


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_probe_script")
public class ProbeScriptModel extends BaseModel implements Serializable {

    
    @ApiModelProperty(value = "探针id")
    private String probeId;

    
    @ApiModelProperty(value = "0:shell 1:python 2:bat")
    private Integer scriptType;

    
    @ApiModelProperty(value = "脚本版本")
    @DefaultInt(1)
    private Integer version;

    
    @ApiModelProperty(value = "脚本状态")
    @DefaultInt(1)
    private Integer status;

    
    @ApiModelProperty(value = "探针脚本内容")
    
    private String scriptContent;

    
    @ApiModelProperty(value = "探针脚本内容fdfs地址")
    private String scriptUrl;

    @ApiModelProperty(hidden = true)
    private String checksum;

}
