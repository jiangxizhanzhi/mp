package cn.cyberpeace.mp.base.service.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class QueryParams implements IdsQueryParams, ExcludeQueryParams, SearchQueryParams, OrderQueryParams,
        PageQueryParams, FieldsQueryParams, CompatiblePageQueryParams, CompatibleOrderQueryParams {
    
    @ApiModelProperty(value = "id列表")
    private String[] ids;
    
    @ApiModelProperty(value = "id排除列表")
    private String[] exclude;
    
    @ApiModelProperty(value = "搜索关键词")
    private String search;
    
    @ApiModelProperty(value = "搜索字段")
    private String[] searchFields;
    
    @ApiModelProperty(value = "排序字段")
    private String[] orders;
    
    @ApiModelProperty(value = "分页页码")
    private Integer page;
    
    @ApiModelProperty(value = "分页每页条数")
    private Integer pageSize;
    
    @ApiModelProperty(value = "指定的返回字段")
    private String[] fields;

    
    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;
    
    @ApiModelProperty(value = "排序字段(兼容)")
    private String[] sort;

    @Override
    public void ensure() {
        CompatiblePageQueryParams.ensureCompatible(this);
        CompatibleOrderQueryParams.ensureCompatible(this);
    }

}
