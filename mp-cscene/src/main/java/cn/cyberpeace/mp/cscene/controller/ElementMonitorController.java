// Copyright (C) 2024
// All rights reserved.
// Project name mp
// Description:
// Author:
// File name ElementMonitorController.java.
// File size 12990.
// File create date Fri Jul 12 12:55:22 CST 2024.
// File update date Fri Jul 12 12:55:22 CST 2024.
// Version 1.0.0
package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.common.JsonCommon;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.repository.condition.HumanCondition;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.TextUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.ConnectionState;
import cn.cyberpeace.mp.cscene.dto.element.ElementHistogramInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementMonitorBasicInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementRTInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityCountInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityInfo;
import cn.cyberpeace.mp.cscene.dto.element.ElementVulnerabilityPieChartInfo;
import cn.cyberpeace.mp.cscene.request.common.BaseSceneElementRequest;
import cn.cyberpeace.mp.cscene.request.element.BaseSceneElementPageRequest;
import cn.cyberpeace.mp.cscene.request.element.ElementMonitorRequest;
import cn.cyberpeace.mp.cscene.service.CsceneHostCollectQueryService;
import cn.cyberpeace.mp.cscene.service.SceneElementMonitorService;
import cn.cyberpeace.mp.sceneplugincollection.bo.CollectionHostEventBO;
import cn.cyberpeace.mp.sceneplugincollection.model.CollectionElementProcessDataModel;
import cn.cyberpeace.mp.sceneplugincollection.repository.CollectionElementProcessDataMapper;
import cn.cyberpeace.mp.situationaware.model.WazuhProcessModel;
import cn.cyberpeace.mp.situationaware.model.wazuh.syscollector.OS;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName ElementMonitorController
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/11/22 10:16
 * @Version 1.0.0
 */
@Api(value = "节点监控", tags = "节点监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/scene/element")
@AuthTarget(TargetEnum.CSCENE_MONITOR_GROUP)
public class ElementMonitorController {

    @Autowired
    private SceneElementMonitorService sceneElementMonitorService;

    @Resource
    private CsceneHostCollectQueryService csceneHostCollectQueryService ;

    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询节点基本信息")
    @GetMapping("/info")
    public ServiceResult<ElementMonitorBasicInfo> getElementMonitorInfo(BaseSceneElementRequest baseRequest) {
        ElementMonitorBasicInfo elementMonitorBasicInfo = sceneElementMonitorService.getElementMonitorInfo(baseRequest, true);

        return ResultCommon.success(elementMonitorBasicInfo);
    }


    /**
     * elementIds不能为空，如果为多个值，默认只取第一个
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "查询节点实时状态")
    @GetMapping("/rtinfo")
    public ServiceResult<ElementRTInfo> getElementRTInfo(BaseSceneElementRequest baseRequest) {
        ElementRTInfo rtInfo = sceneElementMonitorService.getElementRTInfo(baseRequest);
        return ResultCommon.success(rtInfo);
    }


    /**
     * getOS
     *
     * @param baseRequest
     * @return
     */
    @ApiOperation(value = "节点操作系统信息")
    @GetMapping("/os")
    public ServiceResult<OS> getOS(BaseSceneElementRequest baseRequest) {
        OS os = sceneElementMonitorService.getOSInfo(baseRequest);
        return ResultCommon.success(os);
    }


    /**
     * pageConnectionStates
     *
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "查询节点网络连接状态")
    @GetMapping("/connection_state")
    public ListObjServiceResult<ConnectionState> pageConnectionStates(BaseSceneElementPageRequest pageRequest) {
        pageRequest.ensure();

        return sceneElementMonitorService.pageNetworkConnections(pageRequest);
    }

    /**
     * fileModify
     *
     * @param pageRequest
     * @param path
     * @return
     */
    @ApiOperation(value = "查询节点文件变更事件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "文件路径 模糊查询", dataTypeClass = String.class)
    })
    @GetMapping("/file_modify")
    public ListObjServiceResult<CollectionHostEventBO> fileModify(ElementMonitorRequest pageRequest, @RequestParam(required = false) String path) {
        pageRequest.ensure();

        int from = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
        int size = pageRequest.getPageSize();
        String csceneId = pageRequest.getSceneId();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (StringUtils.isNotEmpty(pageRequest.getStartTime())) {
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        if (StringUtils.isNotEmpty(pageRequest.getEndTime())) {
            endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        }
        List<String> nodeIds = pageRequest.getElementIds();
        List<Integer> levels = pageRequest.getLevels();

        var result = csceneHostCollectQueryService.getFileModifyData(from, size, csceneId, nodeIds, startTime, endTime, levels, path);
        return result.asResult();
    }

    /**
     * fileModifyHistogram
     *
     * @param intervalMinutes
     * @param bucketSize
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "节点文件变更事件柱状图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "intervalMinutes", value = "时间间隔,单位分钟,默认15", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "bucketSize", value = "分桶数量,默认15", dataTypeClass = Integer.class)
    })
    @GetMapping("/file_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> fileModifyHistogram(@RequestParam(required = false) Integer intervalMinutes,
                                                                         @RequestParam(required = false) Integer bucketSize,
                                                                         ElementMonitorRequest pageRequest) {
        if (Objects.isNull(intervalMinutes)) {
            intervalMinutes = 15;
        }
        if (Objects.isNull(bucketSize)) {
            bucketSize = 15;
        }
        String csceneId = pageRequest.getSceneId();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (StringUtils.isNotEmpty(pageRequest.getStartTime())) {
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        if (StringUtils.isNotEmpty(pageRequest.getEndTime())) {
            endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        }
        List<String> nodeIds = pageRequest.getElementIds();

        var result = csceneHostCollectQueryService.aggsFileModifyData(csceneId, nodeIds, startTime, endTime, intervalMinutes, bucketSize);
        return new ServiceResult<>(result).asResult();
    }

    /**
     * registryModify
     *
     * @param pageRequest
     * @param path
     * @return
     */
    @ApiOperation(value = "查询节点注册表变更事件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "注册表路径 模糊查询", dataTypeClass = String.class)
    })
    @GetMapping("/registry_modify")
    public ListObjServiceResult<CollectionHostEventBO> registryModify(ElementMonitorRequest pageRequest, @RequestParam(required = false) String path) {
        pageRequest.ensure();

        int from = (pageRequest.getPage() - 1) * pageRequest.getPageSize();
        int size = pageRequest.getPageSize();
        String csceneId = pageRequest.getSceneId();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (StringUtils.isNotEmpty(pageRequest.getStartTime())) {
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        if (StringUtils.isNotEmpty(pageRequest.getEndTime())) {
            endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        }
        List<String> nodeIds = pageRequest.getElementIds();
        List<Integer> levels = pageRequest.getLevels();

        var result = csceneHostCollectQueryService.getRegistryModifyData(from, size, csceneId, nodeIds, startTime, endTime, levels, path);
        return result.asResult();
    }

    /**
     * registryModifyHistogram
     *
     * @param intervalMinutes
     * @param bucketSize
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "节点注册表变更事件柱状图")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "intervalMinutes", value = "时间间隔,单位分钟,默认15", dataTypeClass = Integer.class),
            @ApiImplicitParam(name = "bucketSize", value = "分桶数量,默认15", dataTypeClass = Integer.class)
    })
    @GetMapping("/registry_modify_histogram")
    public ServiceResult<List<ElementHistogramInfo>> registryModifyHistogram(@RequestParam(required = false) Integer intervalMinutes,
                                                                             @RequestParam(required = false) Integer bucketSize,
                                                                             ElementMonitorRequest pageRequest) {
        if (Objects.isNull(intervalMinutes)) {
            intervalMinutes = 15;
        }
        if (Objects.isNull(bucketSize)) {
            bucketSize = 15;
        }

        String csceneId = pageRequest.getSceneId();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (StringUtils.isNotEmpty(pageRequest.getStartTime())) {
            startTime = TimeUtils.stringToLocalDateTime(pageRequest.getStartTime());
        }
        if (StringUtils.isNotEmpty(pageRequest.getEndTime())) {
            endTime = TimeUtils.stringToLocalDateTime(pageRequest.getEndTime());
        }
        List<String> nodeIds = pageRequest.getElementIds();
        var result = csceneHostCollectQueryService.aggsRegistryModifyData(csceneId, nodeIds, startTime, endTime, intervalMinutes, bucketSize);
        return new ServiceResult<>(result).asResult();
    }

    /**
     * getServiceVulnerability
     *
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "查询节点服务漏洞Top10")
    @GetMapping("/get_service_vulnerability")
    public ListObjServiceResult<ElementVulnerabilityCountInfo> getServiceVulnerability(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();

        List<ElementVulnerabilityCountInfo> result = sceneElementMonitorService.getVulnerabilityCountInfo(pageRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }

    /**
     * getVulnerabilityPieChart
     *
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "查询节点服务漏洞饼状图")
    @GetMapping("/get_vulnerability_pie_chart")
    public ListObjServiceResult<ElementVulnerabilityPieChartInfo> getVulnerabilityPieChart(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();

        List<ElementVulnerabilityPieChartInfo> result = sceneElementMonitorService.getVulnerabilityPieChartInfo(pageRequest);
        return new ListObjServiceResult<>(new ListObjResult<>(result, result.size())).asResult();
    }

    /**
     * getVulnerabilityList
     *
     * @param pageRequest
     * @return
     */
    @ApiOperation(value = "查询节点服务漏洞列表")
    @GetMapping("/get_vulnerability_list")
    public ListObjServiceResult<ElementVulnerabilityInfo> getVulnerabilityList(ElementMonitorRequest pageRequest) {
        pageRequest.ensure();

        ListObjResult<ElementVulnerabilityInfo> result = sceneElementMonitorService.getVulnerabilityInfo(pageRequest);
        return new ListObjServiceResult<>(result).asResult();
    }

    @PostMapping("/save_process_data")
    public void saveElementProcessData(@RequestBody Map<String, Object> data, @RequestParam String element) {
        log.debug("save_process_data:" + element);
        log.debug("save_process_data:" + JsonCommon.stringify(data));

        String value = data.containsKey("data") ? data.get("data").toString() : "";
        byte[] raw = TextUtils.rawDecodeBase64(StringUtils.strip(value.trim(), "\""));
        String output = TextUtils.decompress(raw);
        JSONObject jsonObject = JSONObject.parseObject(output);

        JSONArray arr = jsonObject.getJSONArray("deleted_prev");
        List<CollectionElementProcessDataModel> models = new ArrayList<>();
        if (!Objects.isNull(arr)) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data1 = arr.getJSONObject(i);
                WazuhProcessModel processModel = JSONObject.toJavaObject(data1, WazuhProcessModel.class);
                if (Objects.nonNull(jsonObject.get("time"))) {
                    processModel.setTime(TimeUtils.stringToLocalDateTime(jsonObject.get("time").toString(), TimeUtils.dateTimeFormatter));
                }
                if (Objects.nonNull(jsonObject.get("level"))) {
                    processModel.setLevel(jsonObject.getIntValue("level"));
                }
                if (Objects.nonNull(jsonObject.get("description"))) {
                    processModel.setDescription(jsonObject.getString("description"));
                } else {
                    processModel.setDescription("PROCESS");
                }
                CollectionElementProcessDataModel model = new CollectionElementProcessDataModel();
                model.setPid(processModel.getPid());
                model.setData(JsonCommon.stringify(processModel));
                model.setAgentName(element);
                model.setStatus(0);
                models.add(model);
            }
        }

        arr = jsonObject.getJSONArray("values");
        if (!Objects.isNull(arr)) {
            for (int i = 0; i < arr.size(); i++) {
                JSONObject data1 = arr.getJSONObject(i);
                WazuhProcessModel processModel = JSONObject.toJavaObject(data1, WazuhProcessModel.class);
                if (Objects.nonNull(jsonObject.get("time"))) {
                    processModel.setTime(TimeUtils.stringToLocalDateTime(jsonObject.get("time").toString(), TimeUtils.dateTimeFormatter));
                }
                if (Objects.nonNull(jsonObject.get("level"))) {
                    processModel.setLevel(jsonObject.getIntValue("level"));
                }
                if (Objects.nonNull(jsonObject.get("description"))) {
                    processModel.setDescription(jsonObject.getString("description"));
                } else {
                    processModel.setDescription("PROCESS");
                }
                CollectionElementProcessDataModel model = new CollectionElementProcessDataModel();
                model.setPid(processModel.getPid());
                model.setData(JsonCommon.stringify(processModel));
                model.setAgentName(element);
                model.setStatus(1);
                models.add(model);
            }
        }

        var mapper = BeanCommon.getBean(CollectionElementProcessDataMapper.class);

        var condition = new HumanCondition();
        condition.filterEqual(CollectionElementProcessDataModel::getAgentName, element)
                .filterEqual(CollectionElementProcessDataModel::getStatus, 1);
        mapper.filterDelete(condition);

        mapper.bulkCreate(models);
    }
}
