package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName SnapshotPageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class SnapshotPageRequest {
    @ApiModelProperty(value = "分页页码")
    private Integer page = 1;
    @ApiModelProperty(value = "分页大小")
    private Integer perPage = 10;
    @ApiModelProperty(value = "实例id")
    private String instanceId;
}
