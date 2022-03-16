package cn.cyberpeace.mp.cscene.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/1 17:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowGroupByDestinationIpAndSeverityVO {

    @ApiModelProperty("危险等级,0:正常；1：高；2：中；3：中低；4：低")
    private Integer severity;

    @ApiModelProperty("数量")
    private Integer num;
}
