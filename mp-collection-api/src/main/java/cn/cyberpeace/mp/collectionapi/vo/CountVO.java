package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CountVO {
    @ApiModelProperty(value = "名称,跟随请求")
    private String name;

    @ApiModelProperty(value = "数量")
    private long count;
}
