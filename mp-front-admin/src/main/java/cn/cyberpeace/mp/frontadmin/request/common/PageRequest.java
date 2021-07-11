package cn.cyberpeace.mp.frontadmin.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequest {

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;

    
    @ApiModelProperty(value = "排序字段")
    private String[] orders;

    
    @ApiModelProperty(value = "排序字段(兼容)")
    private String[] sort;

    public void ensure() {

        if (perPage != null) {
            pageSize = perPage;
        }

        
        if (Objects.isNull(page) || page <= 1) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }

        if (sort != null && sort.length > 0) {
            orders = sort;
        }
    }

}
