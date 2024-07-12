 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name SysConfigService.java.
 // File size 1363.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.frontadmin.service.sys;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.frontadmin.model.SysConfigModel;

import java.util.List;

/**
 * @ClassName SysConfigService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface SysConfigService extends BaseModelService<SysConfigModel, QueryParams> {

    /**
     * listAll
     *
     * @return
     */
    List<SysConfigModel> listAll();

    /**
     * getByKeyName
     *
     * @param keyName
     * @return
     */
    SysConfigModel getByKeyName(String keyName);

    /**
     * updateByKeyName
     *
     * @param sysConfigModel
     * @return boolean
     */
    boolean updateByKeyName(SysConfigModel sysConfigModel);

    /**
     * batchUpdateByKeyname
     *
     * @param configs
     * @return boolean
     */
    boolean batchUpdateByKeyname(List<SysConfigModel> configs);

    /**
     * 是否需要审核 true（1）:需要 false（0）:不需要
     * @return
     */
    boolean isNewUserNeedAudit();

    /**
     * publishConfigUpdateEvent
     *
     * @param configs
     */
    void publishConfigUpdateEvent(List<SysConfigModel> configs);

}
