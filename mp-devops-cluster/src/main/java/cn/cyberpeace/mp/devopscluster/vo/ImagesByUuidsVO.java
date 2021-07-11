package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "根据UUID集合查询返回信息")
public class ImagesByUuidsVO {

    
    @ApiModelProperty(value = "UUID")
    private String uuid;

    
    @ApiModelProperty(value = "名称")
    private String name;

}
