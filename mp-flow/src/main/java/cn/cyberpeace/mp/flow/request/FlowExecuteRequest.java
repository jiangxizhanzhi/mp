package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowTaskModel;
import cn.cyberpeace.mp.flow.model.base.FlowBaseModel;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量执行请求对象")
public class FlowExecuteRequest extends FlowBaseModel {

    
    private String taskId;

    
    private String uuid;

    
    private int action;

    
    private String urlPath;


    public FlowExecuteRequest(FlowTaskModel flowTaskModel) {
        this.setCount(flowTaskModel.getCount());
        this.setDelay(flowTaskModel.getDelay());
        this.setInterval(flowTaskModel.getIntervalTime());
    }
}
