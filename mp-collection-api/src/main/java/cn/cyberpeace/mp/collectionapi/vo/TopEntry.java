package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TopEntry {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "数量")
    private long count;

    @ApiModelProperty(value = "占比")
    private Double percent;

}
