package cn.cyberpeace.mp.flow.service;

import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.flow.model.EsCollectionDataStorageModel;
import cn.cyberpeace.mp.flow.model.FlowCollectionModel;
import cn.cyberpeace.mp.flow.request.queryparam.FlowCollectionQueryParams;

import java.util.Date;
import java.util.List;

public interface EsCollectionDataStorageService extends BaseModelService<EsCollectionDataStorageModel, QueryParams>  {

    EsCollectionDataStorageModel getByDateAndChannel(String date, String channel);

    List<EsCollectionDataStorageModel> listByDate(String date);
}
