 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CompatibleOrderQueryParams.java.
 // File size 547.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

/**
 * 排序查询参数
 */
public interface CompatibleOrderQueryParams extends OrderQueryParams, CompatibleQueryParams {

    static void ensureCompatible(CompatibleOrderQueryParams queryParams) {
        String[] sort = queryParams.getSort();
        if (sort != null && sort.length > 0) {
            queryParams.setOrders(sort);
        }
    }

    String[] getSort();

    void setSort(String[] sort);

    default void ensure() {
        ensureCompatible(this);
    }
}
