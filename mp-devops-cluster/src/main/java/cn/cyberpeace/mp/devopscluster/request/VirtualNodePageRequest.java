package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VirtualNodePageRequest {
    private Integer page;
    private Integer perPage;
    private String name;
    private String nodeType;
    @ApiModelProperty(value = "是否查询全量数据")
    private Boolean isAll = true;
}
