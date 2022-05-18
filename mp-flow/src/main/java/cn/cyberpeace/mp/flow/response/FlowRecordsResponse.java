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
     * 流量运行状态：0 初始化， 1 运行中， 2 停止中， 3 已停止， 4 未启动过
     */
    private Integer status;

    /**
     * 流量运行结果：1 成功， 2 失败
     */
    private Integer result;

    /**
     * 流量记录列表
     */
    @ApiModelProperty(value = "流量记录列表")
    private List<FlowRecords> flowRecordsList;

}
