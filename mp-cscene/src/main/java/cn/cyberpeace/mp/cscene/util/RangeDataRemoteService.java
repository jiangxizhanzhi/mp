package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.model.SceneCycleDataModel;
import cn.cyberpeace.mp.cscene.repository.SceneCycleDataModelMapper;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.multirange.testrange.TestRangeApiProxy;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 分靶场的数据
 */
@Component("rangeDataRemote")
@Slf4j
public class RangeDataRemoteService {

    @Autowired
    private TestRangeApiProxy testRangeApiProxy;

    @Autowired
    private SceneCycleDataModelMapper sceneCycleDataModelMapper;

    public int period = 180;

    public void savePortInfo(String sceneId, String nodeId) {
        TimeListPageInfoQueryParams listPageInfoQueryParams = new TimeListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(Arrays.asList(sceneId));
        listPageInfoQueryParams.setNodeIds(Arrays.asList(nodeId));
        var result = testRangeApiProxy.pagePorts(listPageInfoQueryParams);
        // 存库
        List<SceneCycleDataModel> dataToSave = new ArrayList<>();
        for (var r : result.getData().getList()) {
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            dataModel.setType(SceneCycleDataModel.PORT);
            dataModel.setData(JsonCommon.stringify(r));
            dataToSave.add(dataModel);
        }
        if (CollectionUtils.isNotEmpty(dataToSave)) {
            // 删除旧的
            sceneCycleDataModelMapper.filterDelete(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                                           .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                                           .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.PORT));
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveProcessInfo(String sceneId, String nodeId) {
        TimeListPageInfoQueryParams listPageInfoQueryParams = new TimeListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(Arrays.asList(sceneId));
        listPageInfoQueryParams.setNodeIds(Arrays.asList(nodeId));
        var result = testRangeApiProxy.pageProcesses(listPageInfoQueryParams);
        // 存库
        List<SceneCycleDataModel> dataToSave = new ArrayList<>();
        for (var r : result.getData().getList()) {
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            dataModel.setType(SceneCycleDataModel.PROCESS);
            dataModel.setData(JsonCommon.stringify(r));
            dataToSave.add(dataModel);
        }
        if (CollectionUtils.isNotEmpty(dataToSave)) {
            // 删除旧的
            sceneCycleDataModelMapper.filterDelete(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                                           .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                                           .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.PROCESS));
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveNetInfo(String sceneId, String nodeId) {
        TimeListPageInfoQueryParams listPageInfoQueryParams = new TimeListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(Arrays.asList(sceneId));
        listPageInfoQueryParams.setNodeIds(Arrays.asList(nodeId));
        var result = testRangeApiProxy.pageNetifaces(listPageInfoQueryParams);
        // 存库
        List<SceneCycleDataModel> dataToSave = new ArrayList<>();
        for (var r : result.getData().getList()) {
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            dataModel.setType(SceneCycleDataModel.NET);
            dataModel.setData(JsonCommon.stringify(r));
            dataToSave.add(dataModel);
        }
        if (CollectionUtils.isNotEmpty(dataToSave)) {
            // 删除旧的
            sceneCycleDataModelMapper.filterDelete(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                                           .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                                           .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.NET));
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveConnectInfo(String sceneId, String nodeId) {
        BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
        request.setSceneId(sceneId);
        request.setElementIds(Arrays.asList(nodeId));
        request.setPage(1);
        request.setPageSize(100);
        var result = testRangeApiProxy.pageNetworkConnections(request);
        // 存库
        List<SceneCycleDataModel> dataToSave = new ArrayList<>();
        for (var r : result.getData().getList()) {
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            dataModel.setType(SceneCycleDataModel.CONNECTION);
            dataModel.setData(JsonCommon.stringify(r));
            dataToSave.add(dataModel);
        }
        if (CollectionUtils.isNotEmpty(dataToSave)) {
            // 删除旧的
            sceneCycleDataModelMapper.filterDelete(new HumanCondition().filterEqual(SceneCycleDataModel::getScene, sceneId)
                                                           .filterEqual(SceneCycleDataModel::getSceneElement, nodeId)
                                                           .filterEqual(SceneCycleDataModel::getType, SceneCycleDataModel.CONNECTION));
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveFileModify(String sceneId, String nodeId) {
        ElementMonitorRequest pageRequest = new ElementMonitorRequest();
        pageRequest.setSceneId(sceneId);
        pageRequest.setElementIds(Arrays.asList(nodeId));
        pageRequest.setPage(1);
        pageRequest.setPageSize(100);
        pageRequest.setStartTime(TimeUtils.localDateTimeToString(LocalDateTime.now().minusSeconds(period)));
        pageRequest.setEndTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
        var result = testRangeApiProxy.getFileModifyData(pageRequest, null);
        List<SceneCycleDataModel> dataToSave = new ArrayList<>();
        for (var r : result.getData().getList()) {
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            dataModel.setType(SceneCycleDataModel.FILE);
            dataModel.setData(JsonCommon.stringify(r));
            dataToSave.add(dataModel);
        }
        if (CollectionUtils.isNotEmpty(dataToSave)) {
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveRegistryModify(String sceneId, String nodeId) {
        ElementMonitorRequest pageRequest = new ElementMonitorRequest();
        pageRequest.setSceneId(sceneId);
        pageRequest.setElementIds(Arrays.asList(nodeId));
        pageRequest.setPage(1);
        pageRequest.setPageSize(100);
        pageRequest.setStartTime(TimeUtils.localDateTimeToString(LocalDateTime.now().minusSeconds(period)));
        pageRequest.setEndTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
        var result = testRangeApiProxy.getRegistryModifyData(pageRequest, null);
        List<SceneCycleDataModel> dataToSave = new ArrayList<>();
        for (var r : result.getData().getList()) {
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            dataModel.setType(SceneCycleDataModel.REGISTRY);
            dataModel.setData(JsonCommon.stringify(r));
            dataToSave.add(dataModel);
        }
        if (CollectionUtils.isNotEmpty(dataToSave)) {
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveLog(String sceneId, String nodeId) {
        HostEventQueryParams hostEventQueryParams = new HostEventQueryParams();
        hostEventQueryParams.setPage(1);
        hostEventQueryParams.setPerPage(200);
        hostEventQueryParams.setStartTime(TimeUtils.localDateTimeToString(LocalDateTime.now().minusSeconds(period)));
        hostEventQueryParams.setEndTime(TimeUtils.localDateTimeToString(LocalDateTime.now()));
        hostEventQueryParams.setNodeIds(Arrays.asList(nodeId));
        var result = testRangeApiProxy.listHostEvents(sceneId, hostEventQueryParams);
        List<SceneCycleDataModel> dataToSave = new ArrayList<>();
        for (var r : result.getData().getList()) {
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            if (StringUtils.isNotEmpty(r.getCatagory())) {
                dataModel.setType(SceneCycleDataModel.LOG + "_" + r.getCatagory());
            } else {
                dataModel.setType(SceneCycleDataModel.LOG + "_");
            }
            dataModel.setData(JsonCommon.stringify(r));
            dataToSave.add(dataModel);
        }
        if (CollectionUtils.isNotEmpty(dataToSave)) {
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveHostInfo(String sceneId, String nodeId) {
        HostStateTrendRequest hostStateTrendRequest = new HostStateTrendRequest();
        hostStateTrendRequest.setSceneId(sceneId);
        hostStateTrendRequest.setElementIds(Arrays.asList(nodeId));
        var response = testRangeApiProxy.getElementHostStateTrend(hostStateTrendRequest);
        var result = response.getData();
        if (CollectionUtils.isNotEmpty(result)) {
            var last = result.get(result.size() - 1);
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement(nodeId);
            dataModel.setType(SceneCycleDataModel.HOST);
            dataModel.setData(JsonCommon.stringify(last));
            sceneCycleDataModelMapper.save(dataModel);
        }
    }

    public void saveHostInfo(String sceneId) {
        HostStateTrendRequest hostStateTrendRequest = new HostStateTrendRequest();
        hostStateTrendRequest.setSceneId(sceneId);
        var result = testRangeApiProxy.getSceneHostStateTrend(hostStateTrendRequest);
        if (CollectionUtils.isNotEmpty(result)) {
            var last = result.get(result.size() - 1);
            SceneCycleDataModel dataModel = new SceneCycleDataModel();
            dataModel.setScene(sceneId);
            dataModel.setSceneElement("");
            dataModel.setType(SceneCycleDataModel.HOST);
            dataModel.setData(JsonCommon.stringify(last));
            sceneCycleDataModelMapper.save(dataModel);
        }
    }

    public void test(String sceneId, boolean info, boolean event, boolean log) {
        List<ElementMonitorBasicInfo> testRangeResult = testRangeApiProxy.listAllElementBasicInfo(sceneId, new SceneAllElementsQueryRequest());
        List<Runnable> tasks = new ArrayList<>();
//        if (info) {
//            tasks.add(() -> saveHostInfo(sceneId));
//        }
        for (ElementMonitorBasicInfo elementMonitorBasicInfo : testRangeResult) {
            String nodeId = elementMonitorBasicInfo.getId();
            if (info) {
                // 信息类 - 端口信息 采集周期到了查询最新数据保存即可
                tasks.add(() -> savePortInfo(sceneId, nodeId));
                // 信息类 - 进程信息 采集周期到了查询最新数据保存即可
                tasks.add(() -> saveProcessInfo(sceneId, nodeId));
                // 信息类 - 网卡信息 采集周期到了查询最新数据保存即可
                tasks.add(() -> saveNetInfo(sceneId, nodeId));
                // 信息类 - 连接信息
                tasks.add(() -> saveConnectInfo(sceneId, nodeId));
                // 信息类 - cpu\内存\磁盘
                tasks.add(() -> saveHostInfo(sceneId, nodeId));
            }

            if (event) {
                // 事件类 - 文件变更 采集周期到了查询最近一个周期的数据保存即可
                tasks.add(() -> saveFileModify(sceneId, nodeId));
                // 事件类 - 注册表变更 采集周期到了查询最近一个周期的数据保存即可
                tasks.add(() -> saveRegistryModify(sceneId, nodeId));
            }

            if (log) {
                // 日志类
                tasks.add(() -> saveLog(sceneId, nodeId));
            }
        }
        TaskCommon.runTasks(tasks);
    }
}
