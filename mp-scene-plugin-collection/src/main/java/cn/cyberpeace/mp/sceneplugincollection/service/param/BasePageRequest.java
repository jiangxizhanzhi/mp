package cn.cyberpeace.mp.sceneplugincollection.service.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Objects;

/**
 * @ClassName BasePageRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2024/07/02
 * @Version 1.0.0
 */
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

    /**
     * @Deprecated 排序字段
     */
    @ApiModelProperty(value = "排序字段(兼容)")
    private String[] sort;


    /**
     * ensure
     */
    public void ensure() {
        if (perPage != null) {
            pageSize = perPage;
        }

        // 默认值
        if (Objects.isNull(page)) {
            page = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }

//        if (sort != null && sort.length > 0) {
//            orders = sort;
//        }
    }

}
