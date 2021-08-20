package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Description: 流量执行请求对象
 * @Autho qiaokang
 * @Date：2021/6/17 09:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量执行请求对象")
public class FlowExecuteRequest extends FlowBaseModel {

    /**
     * 任务id
     */
    private String uuid;

    /**
     * 任务操作类型：1 start， 2 stop, 3 reset
     */
    private int action;

    /**
     * 请求地址路径
     */
    private String urlPath;


    public FlowExecuteRequest(FlowTaskModel flowTaskModel) {
        this.setCount(flowTaskModel.getCount());
        this.setDelay(flowTaskModel.getDelay());
        this.setInterval(flowTaskModel.getIntervalTime());
    }
}
