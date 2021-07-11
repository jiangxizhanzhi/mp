package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest extends BaseRequest implements Serializable {

    
    @Min(1)
    @ApiModelProperty(value = "每页条数",hidden = true)
    private Integer limit;

    
    @Min(1)
    @ApiModelProperty(value = "页码",hidden = true)
    private Integer offset;

    
    @ApiModelProperty(value = "页码")
    private Integer page = 1;

    
    @ApiModelProperty(value = "每页条数")
    private Integer perPage = 10;

    public Integer getLimit() {
        return perPage;
    }

    public Integer getOffset() {
        return page;
    }

    public void ensure() {

        
        if (Objects.isNull(page) || page <= 1) {
            page = 1;
        }

        if (Objects.isNull(perPage)) {
            perPage = 10;
        }

    }
}
