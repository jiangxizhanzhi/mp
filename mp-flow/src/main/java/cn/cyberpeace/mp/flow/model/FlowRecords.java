package cn.cyberpeace.mp.flow.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量记录信息")
public class FlowRecords {

    
    @ApiModelProperty(value = "流量id")
    private Integer id;

    
    @ApiModelProperty(value = "流量执行平台任务id")
    private String taskUuid;

    
    @ApiModelProperty(value = "分组执行次数")
    private Integer groupCount;

    
    @ApiModelProperty(value = "秒")
    private Integer second;

    
    @ApiModelProperty(value = "发包数量")
    private Long opackets;

    
    @ApiModelProperty(value = "发包大小 单位byte")
    private Long obytes;

    
    @ApiModelProperty(value = "每秒发包率 单位为byte/s")
    private Double txBps;

    
    @ApiModelProperty(value = "发包异常数量")
    private Integer oerrors;

    
    @ApiModelProperty(value = "流量发生时间")
    private String recordTime;

    
    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
