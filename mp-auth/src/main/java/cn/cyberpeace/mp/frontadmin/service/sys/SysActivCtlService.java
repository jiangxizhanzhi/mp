 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysActivCtlService.java.
 // File size 859.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysActivCtlModel;

/**
 * @ClassName SysActivCtlService
 * @Description
 * @Author zhouxinghuai
 * @Date 2022/2/15 14:18
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SysActivCtlService extends BaseModelService<SysActivCtlModel, QueryParams> {

    /**
     * selectValid
     *
     * @return
     */
    SysActivCtlModel selectValid();

    /**
     * selectUnused
     *
     * @return
     */
    SysActivCtlModel selectUnused();

    /**
     * set all records' status = 0 except ${exceptedId}
     * @param exceptedId
     * @return
     */
    void invalidOtherExceptOne(String exceptedId);

}
