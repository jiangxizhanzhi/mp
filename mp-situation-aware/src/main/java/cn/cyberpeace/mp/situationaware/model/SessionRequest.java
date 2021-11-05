package cn.cyberpeace.mp.situationaware.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("流量查询条件")
public class SessionRequest {

    /**
     * 查询开始时间
     */
    @ApiModelProperty("开始时间-yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 查询
     */
    @ApiModelProperty("结束时间-yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 查询条件需要的分组
     */
    @NotNull(message = "分组id列表不能为空")
    @ApiModelProperty("分组")
    private List<String> groupList;

    @ApiModelProperty("源IP")
    private String sourceIp;

    @ApiModelProperty("源端口")
    private String srcPort;

    @ApiModelProperty("目的IP")
    private String targetIp;

    @ApiModelProperty("目的端口")
    private String dstPort;

    /**
     * 查询条数默认限制
     */
    @ApiModelProperty("分页数量：不走分页场景的时候默认返回100条数据")
    private Integer pageSize = 100;

    /**
     * 分页页码
     */
    @ApiModelProperty("分页页码")
    private Integer pageNum = 1;

    /**
     * 协议
     */
    @ApiModelProperty("协议 模糊查询字段")
    private String protocol;


    @ApiModelProperty("场景source macId")
    private List<String> macIds;
}
