package cn.cyberpeace.mp.cscene.request.flow;

import cn.cyberpeace.mp.auth.request.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 查询流量任务列表请求体
 * @Author: guquan
 * @Date：2023/5/13 14:27
 */
@Data
@ApiModel("查询流量任务列表请求体")
public class FlowTaskPageRequest extends PageRequest {

    @ApiModelProperty("场景实例id")
    private String id;
}
