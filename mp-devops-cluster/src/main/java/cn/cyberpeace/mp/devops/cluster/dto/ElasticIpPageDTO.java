package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.request.ElasticIpPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 弹性IP分页请求信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ElasticIpPageDTO extends BasePageDTO{

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    public ElasticIpPageDTO(ElasticIpPageRequest request){
        this.name = request.getName();
        super.setLimit(request.getLimit());
        super.setOffset(request.getOffset());
    }
}
