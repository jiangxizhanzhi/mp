package cn.cyberpeace.mp.flow.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 流量记录
 * @Author qiaokang
 * @Date：2022/4/21 14:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量记录信息")
public class FlowRecords {

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    private Integer id;

    /**
     * 流量执行平台任务id
     */
    @ApiModelProperty(value = "流量执行平台任务id")
    private String taskUuid;

    /**
     * 分组执行次数
     */
    @ApiModelProperty(value = "分组执行次数")
    private Integer groupCount;

    /**
     * 秒
     */
    @ApiModelProperty(value = "秒")
    private Integer second;

    /**
     * 发包数量
     */
    @ApiModelProperty(value = "发包数量")
    private Integer opackets;

    /**
     * 发包大小
     * 单位为 byte
     */
    @ApiModelProperty(value = "发包大小 单位byte")
    private Integer obytes;

    /**
     * 每秒发包率
     * 单位为 byte/s
     */
    @ApiModelProperty(value = "每秒发包率 单位为byte/s")
    private Double txBps;

    /**
     * 发包异常数量
     */
    @ApiModelProperty(value = "发包异常数量")
    private Integer oerrors;

    /**
     * 流量发生时间
     */
    @ApiModelProperty(value = "流量发生时间")
    private String recordTime;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

}
