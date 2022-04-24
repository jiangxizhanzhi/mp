package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针信息")
public class ProbeResult {
    private Integer pk;

    private String id;

    @ApiModelProperty(value = "探针名称", required = true)
    private String probeName;

    @ApiModelProperty(value = "探针描述信息", required = true)
    private String des;

    @ApiModelProperty(value = "操作系统类型0 linux 1 windows")
    private Integer osType;

    @ApiModelProperty(value = "探针分类 0:信息类 1:日志类 2: 事件类", required = true)
    private Integer probeCategory;

    @ApiModelProperty(value = "探针脚本类型 0:shell 1:python 2:bat")
    private Integer scriptType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "脚本内容")
    private String scriptContent;

    @ApiModelProperty(value = "调度周期 单位分钟")
    private Integer commandInterval;

    @ApiModelProperty(value = "探针状态 0:未开始 1：运行中 2：已关闭")
    private Integer probeStatus;

    private List<ProbeParamsResult> paramsResultList;

    @ApiModelProperty(value = "探针数据输出格式 json plain none")
    private String outputFormat;
}
