package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "服务管理的删除请求参数")
public class ServiceManageDeleteRequest extends BaseRequest {

    /**
     * ids
     */
    @ApiModelProperty(value = "id集合")
    private List<String> ids;

}
