// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name QueryParams.java.
// File size 2173.
// File create date Fri Jul 12 12:55:31 CST 2024.
// File update date Fri Jul 12 12:55:31 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QueryParams
 * @Description 查询参数定义
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see IdsQueryParams
 * @see ExcludeQueryParams
 * @see SearchQueryParams
 * @see OrderQueryParams
 * @see PageQueryParams
 * @see FieldsQueryParams
 * @see CompatiblePageQueryParams
 * @see CompatibleOrderQueryParams
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class QueryParams {
    /**
     * id列表
     */
    @ApiModelProperty(value = "id列表")
    private String[] ids;
    /**
     * id排除列表
     */
    @ApiModelProperty(value = "id排除列表")
    private String[] exclude;
    /**
     * 搜索关键词
     */
    @ApiModelProperty(value = "搜索关键词")
    private String search;
    /**
     * 搜索字段
     */
    @ApiModelProperty(value = "搜索字段")
    private String[] searchFields;
    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段")
    private String[] orders;
    /**
     * 分页页码
     */
    @ApiModelProperty(value = "分页页码")
    private Integer page;
    /**
     * 分页每页条数
     */
    @ApiModelProperty(value = "分页每页条数")
    private Integer pageSize;
    /**
     * 指定的返回字段
     */
    @ApiModelProperty(value = "指定的返回字段")
    private String[] fields;

    /**
     * @Deprecated 分页每页条数
     */
    @ApiModelProperty(value = "分页每页条数(兼容)")
    private Integer perPage;
    /**
     * @Deprecated 排序字段
     */
    @ApiModelProperty(value = "排序字段(兼容)")
    private String[] sort;

    public Integer getPageSize() {
        if (pageSize != null) {
            return pageSize;
        } else if (perPage != null) {
            return perPage;
        } else {
            return 10;
        }
    }

    public Integer getPerPage() {
        if (pageSize != null) {
            return pageSize;
        } else if (perPage != null) {
            return perPage;
        } else {
            return 10;
        }
    }

    public Integer getPage() {
        return page == null ? 1 : page;
    }
}
