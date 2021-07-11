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
@ApiModel(value = "批量删除流量信息请求")
public class BatchDeleteFlowInfoRequest {

    
    @NotEmpty(message = "idList can not be empty")
    @ApiModelProperty("流量信息id集合")
    private List<String> idList;

}
