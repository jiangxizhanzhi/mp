 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PageQueryParams.java.
 // File size 611.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName PageQueryParams
 * @Description 分页查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface PageQueryParams {

    /**
     * getPage
     *
     * @return
     */
    Integer getPage();

    /**
     * getPageSize
     *
     * @return
     */
    Integer getPageSize();

    /**
     * setPage
     *
     * @param page
     */
    void setPage(Integer page);

    /**
     * setPageSize
     *
     * @param pageSize
     */
    void setPageSize(Integer pageSize);

}
