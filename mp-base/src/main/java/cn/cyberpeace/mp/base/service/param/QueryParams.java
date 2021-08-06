package cn.cyberpeace.mp.base.service.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class QueryParams {
    /** id列表 */
    @ApiModelProperty(value = "id列表")
    private String[] ids;
    /** 搜索关键词 */
    @ApiModelProperty(value = "搜索关键词")
    private String search;
    /** 搜索字段 */
    @ApiModelProperty(value = "搜索字段")
    private String[] searchFields;
    /** 排序字段 */
    @ApiModelProperty(value = "排序字段")
    private String[] orders;
    /** 分页页码 */
    @ApiModelProperty(value = "分页页码")
    private Integer page;
    /** 分页每页条数 */
    @ApiModelProperty(value = "分页每页条数")
    private Integer pageSize;
    /** 指定的返回字段 */
    @ApiModelProperty(value = "指定的返回字段")
    private String[] fields;

    /** @Deprecated 分页每页条数 */
    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;

    public void ensure() {
        if (perPage != null) {
            pageSize = perPage;
        }
    }

}
