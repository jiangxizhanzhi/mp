package cn.cyberpeace.mp.datasandtable.model;

import cn.cyberpeace.mp.datasandtable.model.enums.OsType;
import cn.cyberpeace.mp.datasandtable.model.enums.ScriptType;
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

    @ApiModelProperty(value = "探针名称", required = true)
    @NotNull(message = "探针名称不能为空")
    private String probeName;

    @ApiModelProperty(value = "探针描述")
    private String des;

    @ApiModelProperty(value = "操作系统类型 0:linux 1:windows", required = true)
    @NotNull(message = "操作系统类型不能为空")
    private OsType osType;

    @ApiModelProperty(value = "探针所属分类", required = true)
    @NotNull(message = "探针所属分类不能为空")
    private Integer probeCategory;

    @ApiModelProperty(value = "探针脚本类型 0:shell 1:python 2:bat", required = true)
    @NotNull(message = "探针脚本类型不能为空")
    private ScriptType scriptType;

    @ApiModelProperty(value = "探针id,新增的时候不需要传")
    private String probeId;

    @ApiModelProperty(value = "探针脚本内容", required = true)
    private String scriptContent;

    @ApiModelProperty(value = "单位分钟", required = true)
    private Integer commandInterval;

    @ApiModelProperty(value = "脚本参数 建议 ##包裹参数，方便替换", required = true)
    private List<ProbeParamsRequest> probeParamsRequestList;
}
