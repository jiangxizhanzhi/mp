package cn.cyberpeace.mp.scenepluginflow.service.response;

import cn.cyberpeace.mp.flow.model.FlowRecords;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description: 流量记录返回信息
 * @Author hewei
 * @Date：2022/5/12 16:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量记录返回信息")
public class SceneFlowRecordsResponse {

    /**
     * 流量记录列表
     */
    @ApiModelProperty(value = "流量记录列表")
    private List<FlowRecords> flowRecordsList;

}
