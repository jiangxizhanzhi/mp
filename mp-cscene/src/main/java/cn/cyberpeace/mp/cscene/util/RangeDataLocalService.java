package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.model.SceneCycleDataModel;
import cn.cyberpeace.mp.cscene.repository.SceneCycleDataModelMapper;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.cscene.service.SceneMonitorService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.hutool.cron.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 本靶场的数据
 */
@Component("rangeDataLocalService")
public class RangeDataLocalService {
    @Autowired
    private CsceneInstanceService csceneInstanceService;

    @Autowired
    private HostStateService hostStateService;

    @Autowired
    private SceneMonitorService sceneMonitorService;

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Autowired
    private SceneCycleDataModelMapper sceneCycleDataModelMapper;

    public int period = 180;

    public void savePortInfo(String sceneId, String nodeId) {
        TimeListPageInfoQueryParams listPageInfoQueryParams = new TimeListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(Arrays.asList(sceneId));
        listPageInfoQueryParams.setNodeIds(Arrays.asList(nodeId));
        var result = hostStateService.pagePorts(listPageInfoQueryParams).asResult();
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
                                                           .filterEqual(SceneCycleDataModel::getType, 1));
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveProcessInfo(String sceneId, String nodeId) {
        TimeListPageInfoQueryParams listPageInfoQueryParams = new TimeListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(Arrays.asList(sceneId));
        listPageInfoQueryParams.setNodeIds(Arrays.asList(nodeId));
        var result = hostStateService.pageProcesses(listPageInfoQueryParams).asResult();
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
                                                           .filterEqual(SceneCycleDataModel::getType, 2));
            sceneCycleDataModelMapper.bulkCreate(dataToSave);
        }
    }

    public void saveNetInfo(String sceneId, String nodeId) {
        TimeListPageInfoQueryParams listPageInfoQueryParams = new TimeListPageInfoQueryParams();
        listPageInfoQueryParams.setSceneIds(Arrays.asList(sceneId));
        listPageInfoQueryParams.setNodeIds(Arrays.asList(nodeId));
        var result = hostStateService.pageNetifaces(listPageInfoQueryParams).asResult();
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
        var result = sceneElementMonitorService.pageNetworkConnections(request).asResult();
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
        var result = sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.FILE_MODIFY_GROUP, null)
                .asResult();
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
        var result = sceneElementMonitorService.getModifyData(pageRequest, CsceneBizConstant.REGISTRY_MODIFY_GROUP, null)
                .asResult();
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
        var result = csceneInstanceService.listHostEvents(sceneId, hostEventQueryParams).asResult();
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

    public void test(String sceneId, boolean info, boolean event, boolean log) {
        List<ElementMonitorBasicInfo> basicInfoList = sceneMonitorService.getAllSceneElements(sceneId, false, new SceneAllElementsQueryRequest());
        List<Runnable> tasks = new ArrayList<>();
        for (ElementMonitorBasicInfo elementMonitorBasicInfo : basicInfoList) {
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
