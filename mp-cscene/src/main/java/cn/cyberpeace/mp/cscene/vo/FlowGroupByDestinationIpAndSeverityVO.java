package cn.cyberpeace.mp.cscene.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/1 17:21
 */
@Data
public class FlowGroupByDestinationIpAndSeverityVO {

    @ApiModelProperty("危险等级")
    private Integer severity;

    @ApiModelProperty("数量")
    private Integer num;
}
