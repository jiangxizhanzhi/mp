 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name BaseConverter.java.
 // File size 846.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devopscluster.request.BasePageRequest;

/**
 * @ClassName BaseConverter
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public interface BaseConverter {

    /**
     * getDefaultPageIgnoreField
     *
     * @return
     */
    default String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getPage,BasePageRequest::getPerPage);
    }

    /**
     * getTotalPageIgnoreField
     *
     * @return
     */
    default String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(BasePageRequest::getPage,
                BasePageRequest::getPage,BasePageRequest::getPerPage);
    }
}
