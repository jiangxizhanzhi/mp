 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name CompatibleOrderQueryParams.java.
 // File size 949.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName CompatibleOrderQueryParams
 * @Description 排序查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see OrderQueryParams
 * @see CompatibleQueryParams
 */
public interface CompatibleOrderQueryParams extends OrderQueryParams, CompatibleQueryParams {

    /**
     * ensureCompatible
     *
     * @param queryParams
     */
    static void ensureCompatible(CompatibleOrderQueryParams queryParams) {
        String[] sort = queryParams.getSort();
        if (sort != null && sort.length > 0) {
            queryParams.setOrders(sort);
        }
    }

    /**
     * getSort
     *
     * @return
     */
    String[] getSort();

    /**
     * setSort
     *
     * @param sort
     */
    void setSort(String[] sort);

    /**
     * ensure
     */
    default void ensure() {
        ensureCompatible(this);
    }
}
