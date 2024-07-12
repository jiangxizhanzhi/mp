 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name EsCollectionDataStorageService.java.
 // File size 1034.
 // File create date Fri Jul 12 12:55:24 CST 2024.
 // File update date Fri Jul 12 12:55:24 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.EsCollectionDataStorageModel;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionQueryParams;

import java.util.Date;
import java.util.List;

/**
 * @ClassName EsCollectionDataStorageService
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface EsCollectionDataStorageService extends BaseModelService<EsCollectionDataStorageModel, QueryParams>  {

    /**
     * getByDateAndChannel
     *
     * @param date
     * @param channel
     * @return
     */
    EsCollectionDataStorageModel getByDateAndChannel(String date, String channel);

    /**
     * listByDate
     *
     * @param date
     * @return
     */
    List<EsCollectionDataStorageModel> listByDate(String date);
}
