 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CompatiblePageQueryParams.java.
 // File size 568.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

/**
 * 分页兼容查询参数
 */
public interface CompatiblePageQueryParams extends PageQueryParams, CompatibleQueryParams {

    static void ensureCompatible(CompatiblePageQueryParams queryParams) {
        Integer perPage = queryParams.getPerPage();
        if (perPage != null) {
            queryParams.setPageSize(perPage);
        }
    }

    Integer getPerPage();

    void setPerPage(Integer perPage);

    @Override
    default void ensure() {
        ensureCompatible(this);
    }

}
