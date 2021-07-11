package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DynamipsInstancePageRequest extends InstancePageRequest {

    @ApiModelProperty(value = "排序字段")
    private String order;

    @ApiModelProperty(value = "升序:asc,倒序:desc")
    private String sort;
}
