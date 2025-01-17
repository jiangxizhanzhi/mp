 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name PageQueryParams.java.
 // File size 256.
 // File create date Tue Sep 24 15:29:08 CST 2024.
 // File update date Fri Jan 17 11:23:11 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

/**
 * 分页查询参数
 */
public interface PageQueryParams {

    Integer getPage();

    Integer getPageSize();

    void setPage(Integer page);

    void setPageSize(Integer pageSize);

}
