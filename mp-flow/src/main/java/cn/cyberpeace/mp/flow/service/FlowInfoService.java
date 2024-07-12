 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowInfoService.java.
 // File size 651.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowInfoQueryParams;

import java.util.List;

/**
 * @ClassName FlowInfoService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface FlowInfoService extends BaseModelService<FlowInfoModel, FlowInfoQueryParams>  {

    /**
     * 根据id集合查询列表
     *
     * @param idList
     * @return
     */
    List<FlowInfoModel> findByIds(List<String> idList);

}
