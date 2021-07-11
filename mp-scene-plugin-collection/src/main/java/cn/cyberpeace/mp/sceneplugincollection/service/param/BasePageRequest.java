package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;


@Data
public class BasePageRequest {

    @ApiModelProperty(value = "当前页")
    private Integer page;

    @ApiModelProperty(value = "每页数量")
    private Integer pageSize;

    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;

    @ApiModelProperty(value = "返回字段")
    private String[] select;

    
    @ApiModelProperty(value = "排序字段(兼容)")
    private String[] sort;


    public void ensure() {
        if (perPage != null) {
            pageSize = perPage;
        }

        
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }




    }

}
