package cn.cyberpeace.mp.devopscluster.dto;

import cn.cyberpeace.mp.devopscluster.request.ElasticIpPageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @ClassName ElasticIpPageDTO
 * @Description 弹性IP分页请求信息
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BasePageDTO
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
        super.setLimit(request.getPerPage());
        super.setOffset(request.getPage());
    }
}
