package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 11:13
 */
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

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private String[] orders;

    /**
     * @Deprecated 排序字段
     */
    @ApiModelProperty(value = "排序字段(兼容)")
    private String[] sort;

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

        if (sort != null && sort.length > 0) {
            orders = sort;
        }
    }

}
