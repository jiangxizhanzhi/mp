package cn.cyberpeace.mp.cscene.request.load;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 载荷执行结果查询
 */
@Data
@NoArgsConstructor
public class LoadResultExportRequest {

    @ApiModelProperty(value = "工具pks")
    private List<String> pks;

    @ApiModelProperty(value = "工具pk")
    private String pk;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String overTime;

    @ApiModelProperty(value = "场景Id")
    private String sceneId;

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "分页每页条数")
    private Integer perPage;

    @ApiModelProperty(value = "导出格式(1:xlsx 2:json 3:xml)")
    private Integer exportType;

    @ApiModelProperty(value = "阶段")
    private List<EvalCodeStageRsp> stages;

    @JsonIgnore
    private Integer offset;

    @JsonIgnore
    private Integer pageSize;
}