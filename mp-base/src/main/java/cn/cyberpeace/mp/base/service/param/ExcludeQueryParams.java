 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ExcludeQueryParams.java.
 // File size 440.
 // File create date Fri Jul 12 12:55:31 CST 2024.
 // File update date Fri Jul 12 12:55:31 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.base.service.param;

/**
 * @ClassName ExcludeQueryParams
 * @Description id排除列表查询参数
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface ExcludeQueryParams {

    /**
     * getExclude
     *
     * @return
     */
    String[] getExclude();

    /**
     * setExclude
     *
     * @param exclude
     */
    void setExclude(String[] exclude);

}
