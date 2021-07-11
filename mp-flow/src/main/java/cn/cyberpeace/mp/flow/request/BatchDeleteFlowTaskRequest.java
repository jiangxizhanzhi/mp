package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "批量删除流量任务请求")
public class BatchDeleteFlowTaskRequest {

    
    @NotEmpty(message = "idList can not be empty")
    @ApiModelProperty("流量任务id集合")
    private List<String> idList;

}
