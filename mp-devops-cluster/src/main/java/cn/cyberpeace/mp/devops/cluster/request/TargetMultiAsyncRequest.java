package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TargetMultiAsyncRequest extends BasePageRequest{

    /**
     * 线程数量
     */
    @ApiModelProperty("线程数量")
    private Integer threadCount;
}
