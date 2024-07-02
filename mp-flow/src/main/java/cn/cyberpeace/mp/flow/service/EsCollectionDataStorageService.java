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
