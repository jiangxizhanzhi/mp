package cn.cyberpeace.mp.situationaware.service;


/**
 *@Desciption 数据采集流表管理
 *@Author zhangjin
 *@Date 11/22/21 10:54 上午
 **/
public interface IDataCollectionFlowService {

    void createDataCollectionFlow(String agentInstanceId);

    void deleteDataCollectionFlow(String agentInstanceId);
}
