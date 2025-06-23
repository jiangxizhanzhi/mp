package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.common.TaskCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.constant.CsceneBizConstant;
import cn.cyberpeace.mp.cscene.controller.request.SceneAllElementsQueryRequest;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.HostState;
import cn.cyberpeace.mp.cscene.model.SceneCycleDataModel;
import cn.cyberpeace.mp.cscene.repository.SceneCycleDataModelMapper;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.request.hoststate.HostStateTrendRequest;
import cn.cyberpeace.mp.cscene.service.CsceneInstanceService;
import cn.cyberpeace.mp.cscene.service.HostStateService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.cscene.service.SceneMonitorService;
import cn.cyberpeace.mp.sceneplugincollection.service.param.HostEventQueryParams;
import cn.cyberpeace.mp.sceneplugincollection.service.param.TimeListPageInfoQueryParams;
import cn.hutool.cron.task.Task;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    public void saveHostInfo(String sceneId,LocalDateTime currentTime) {
        BaseSceneElementPageRequest request = new BaseSceneElementPageRequest();
        request.setSceneId(sceneId);
        request.setPage(1);
        request.setPageSize(100);

        HostStateTrendRequest hostStateTrendRequest = new HostStateTrendRequest();
        hostStateTrendRequest.setStartTime(LocalDateTime.now().minusMinutes(5));
        hostStateTrendRequest.setEndTime(LocalDateTime.now());
        hostStateTrendRequest.setSceneId(sceneId);
        var result = hostStateService.getSceneHostStateTrend(hostStateTrendRequest);
        if (CollectionUtils.isNotEmpty(result)) {
            for (var tmp : result) {
                if (tmp.getCpuPercent() > 0) {
                    tmp.setTime(currentTime);
                    SceneCycleDataModel dataModel = new SceneCycleDataModel();
                    dataModel.setScene(sceneId);
                    dataModel.setSceneElement("");
                    dataModel.setType(SceneCycleDataModel.HOST);
                    dataModel.setData(JsonCommon.stringify(tmp));
                    sceneCycleDataModelMapper.save(dataModel);
                    break;
                }
            }
        }
    }

    public void saveHostInfo(String sceneId, String nodeId,LocalDateTime currentTime) {
        log.info("查询主机数据: {} , {} ", sceneId, nodeId);
        HostStateTrendRequest hostStateTrendRequest = new HostStateTrendRequest();
        hostStateTrendRequest.setSceneId(sceneId);
        hostStateTrendRequest.setElementIds(Arrays.asList(nodeId));
        hostStateTrendRequest.setStartTime(LocalDateTime.now().minusMinutes(5));
        hostStateTrendRequest.setEndTime(LocalDateTime.now());
        var result = hostStateService.getElementHostStateTrend(hostStateTrendRequest);
        if (CollectionUtils.isNotEmpty(result)) {
            for (var tmp : result) {
                if (tmp.getCpuPercent() != null && tmp.getCpuPercent() != 0) {
                    var last = tmp;
                    last.setTime(currentTime);
                    SceneCycleDataModel dataModel = new SceneCycleDataModel();
                    dataModel.setScene(sceneId);
                    dataModel.setSceneElement(nodeId);
                    dataModel.setType(SceneCycleDataModel.HOST);
                    dataModel.setData(JsonCommon.stringify(last));
                    sceneCycleDataModelMapper.save(dataModel);
                    break;
                }
            }
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

    public void test(String sceneId, boolean info, boolean event, boolean log, LocalDateTime currentTime ) {
        List<ElementMonitorBasicInfo> basicInfoList = sceneMonitorService.getAllSceneElements(sceneId, false, new SceneAllElementsQueryRequest());
        List<Runnable> tasks = new ArrayList<>();
        saveHostInfo(sceneId, "acddbb13-74c6-445f-b88e-fb7847e9514b",currentTime);
    }
}
