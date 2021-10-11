package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.situationaware.model.enums.SourceCategory;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针管理查询参数")
public class ProbeSearchRequest {

    /**
     * 查询条数默认限制
     */
    @ApiModelProperty("默认分页数量")
    @Min(1)
    @Builder.Default
    private Integer pageSize = 10;

    /**
     * 分页页码
     */
    @ApiModelProperty("分页页码")
    @Min(1)
    @Builder.Default
    private Integer pageNum = 1;

    @ApiModelProperty("关键字查询")
    private String keyWord;

    @ApiModelProperty("是否是定制探针 0：默认探针 1：定制探针")
    private SourceCategory sourceCategory;
}
