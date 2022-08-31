package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: 业务监控分页查询
 * @Author qiaokang
 * @Date：2022/7/15 14:56
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BizServicePageRequest extends BasePageRequest implements Serializable {

    /**
     * 节点名称
     */
    @ApiModelProperty(value = "节点名称")
    private String hostName;

    /**
     * 如 +runtime or -runtime
     */
    @ApiModelProperty(value = "排序方式")
    private String sort;

}
