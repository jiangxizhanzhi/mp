package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 删除流量信息请求
 * @Author qiaokang
 * @Date：2021/8/10 15:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "删除流量信息请求")
public class DeleteFlowInfoRequest {

    /**
     * 流量信息id
     */
    @ApiModelProperty("流量信息id")
    private String id;


}
