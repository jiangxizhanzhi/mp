package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowRecords;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 流量记录返回信息
 * @Author qiaokang
 * @Date：2022/4/21 13:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量记录返回信息")
public class FlowRecordsResponse {

    /**
     * 流量记录列表
     */
    @ApiModelProperty(value = "流量记录列表")
    private List<FlowRecords> flowRecordsList;

}
