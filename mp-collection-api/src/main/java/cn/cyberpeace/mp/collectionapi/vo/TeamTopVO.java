package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TeamTopVO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "目的列表")
    private List<TopEntry> entries;

}
