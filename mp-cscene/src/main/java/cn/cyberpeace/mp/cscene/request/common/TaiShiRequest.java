package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaiShiRequest extends BaseTimeRangeRequest {
    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "攻防类型")
    private List<String> defenseTypes;

    @ApiModelProperty(value = "攻击源节点,不传默认查询全部")
    private List<String> sourceNodeIds;

    @ApiModelProperty(value = "攻击目标节点,不传默认查询全部")
    private List<String> targetNodeIds;

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }
}
