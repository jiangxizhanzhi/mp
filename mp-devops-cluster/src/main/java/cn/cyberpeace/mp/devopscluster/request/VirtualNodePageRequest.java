package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName VirtualNodePageRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class VirtualNodePageRequest {
    private Integer page;
    private Integer perPage;
    private String name;
    private String nodeType;
    @ApiModelProperty(value = "是否查询全量数据")
    private Boolean isAll = true;
}
