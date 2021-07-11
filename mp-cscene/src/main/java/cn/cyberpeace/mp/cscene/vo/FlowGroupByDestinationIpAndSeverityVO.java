package cn.cyberpeace.mp.cscene.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowGroupByDestinationIpAndSeverityVO {

    @ApiModelProperty("危险等级,0:正常；1：高；2：中；3：低；4：低")
    private Integer severity;

    @ApiModelProperty("数量")
    private Integer num;
}
