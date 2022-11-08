package cn.cyberpeace.mp.sceneplugincollection.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/12/1 17:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlowGroupByDestinationIpAndSeverityBO {

    @ApiModelProperty("危险等级")
    private Integer severity;

    @ApiModelProperty("目的IP集合")
    private List<String> destinationIpList;

    private Integer num;

    public FlowGroupByDestinationIpAndSeverityBO(Integer severity, Integer num) {
        this.severity = severity;
        this.num = num;
    }
}
