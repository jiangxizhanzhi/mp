package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.situationaware.model.enums.OsType;
import cn.cyberpeace.mp.situationaware.model.enums.ScriptType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针管理入参")
public class ProbeRequest {

    @ApiModelProperty(value = "探针名称")
    private String probeName;

    @ApiModelProperty(value = "探针描述")
    private String des;

    @ApiModelProperty(value = "操作系统类型 0:linux 1:windows")
    private OsType osType;

    @ApiModelProperty(value = "探针所属分类")
    private Integer probeCategory;

    @ApiModelProperty(value = "探针脚本类型 0:shell 1:python 2:bat")
    private ScriptType scriptType;

    @ApiModelProperty(value = "探针id,新增的时候不需要传")
    private String probeId;

    @ApiModelProperty(value = "探针脚本内容")
    private String scriptContent;

    @ApiModelProperty(value = "单位分钟")
    private Integer commandInterval;

    @ApiModelProperty(value = "脚本参数 建议 ##包裹参数，方便替换")
    private List<ProbeParamsRequest> paramsResultList;
}
