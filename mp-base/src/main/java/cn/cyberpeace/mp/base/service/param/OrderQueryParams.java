 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name OrderQueryParams.java.
 // File size 422.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName OrderQueryParams
 * @Description 排序查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface OrderQueryParams {

    /**
     * getOrders
     *
     * @return
     */
    String[] getOrders();

    /**
     * setOrders
     *
     * @param fields
     */
    void setOrders(String[] fields);

}
