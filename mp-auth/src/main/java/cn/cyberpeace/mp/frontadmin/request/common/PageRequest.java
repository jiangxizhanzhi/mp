 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PageRequest.java.
 // File size 1322.
 // File create date Fri Jul 12 12:55:34 CST 2024.
 // File update date Fri Jul 12 12:55:34 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @ClassName PageRequest
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
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
