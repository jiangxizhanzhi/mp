package cn.cyberpeace.mp.cscene.response;

import cn.cyberpeace.mp.flow.bo.FlowTaskConf;
import cn.cyberpeace.mp.flow.model.FlowTaskConfModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 查询流量任务配置响应体
 * @Author: guquan
 * @Date：2023/5/9 20:45
 */
@Data
@ApiModel("查询流量任务配置响应体")
public class QueryFlowTaskConfResponse extends FlowTaskConfModel {

    /**
     * 流量任务配置
     */
    @ApiModelProperty(value = "流量任务配置")
    private FlowTaskConf flowTaskConf;
}
