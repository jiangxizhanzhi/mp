package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.collectionapi.BO.HostBO;
import cn.cyberpeace.mp.collectionapi.BO.ProcessBO;
import cn.cyberpeace.mp.collectionapi.BO.SystemUserBO;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.config.GroupConfig;
import cn.cyberpeace.mp.collection.model.FileEventModel;
import cn.cyberpeace.mp.collection.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.request.HostMonitorPageRequest;
import cn.cyberpeace.mp.collection.request.SyscheckEventRequest;
import cn.cyberpeace.mp.collection.service.DataCollectServiceImpl;
import cn.cyberpeace.mp.collection.service.HostMonitorServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.cyberpeace.mp.collectionapi.request.PageQueryRequest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wazuh")
@Api(value = "态势感知-事件查询接口", tags = "态势感知-事件查询接口")
public class DataCollectionController {
    private static GroupConfig groupConfig;

    @Resource
    private DataCollectServiceImpl dataCollectService;

    @Resource
    private HostMonitorServiceImpl hostMonitorService;

    @GetMapping("/file_events")
    @ApiOperation(value = "查询文件变更事件", notes = "")
    public ListObjServiceResult<FileEventModel> queryFileEvents(PageQueryRequest request) {
        var group = getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        processQueryRequest(request, group);
        SyscheckEventRequest eventRequest = new SyscheckEventRequest();
        eventRequest.setPageNum(request.getPage());
        eventRequest.setPageSize(request.getPageSize());
        eventRequest.setAgentNames(request.getAgentNames());
        if (Objects.nonNull(request.getStartTime())) {
            eventRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));
            if (Objects.nonNull(request.getEndTime())) {
                eventRequest.setEndTime(TimeUtils.stringToLocalDateTime(request.getEndTime()));
            } else {
                eventRequest.setEndTime(LocalDateTime.now());
            }
        }
        return dataCollectService.queryFileModifyEvents(eventRequest).asResult();
    }

    @GetMapping("/process_info")
    @ApiOperation(value = "查询进程信息", notes = "")
    public ListObjServiceResult<ProcessBO> queryProcessInfo(PageQueryRequest request) {
        var group = getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        processQueryRequest(request, group);
        HostMonitorPageRequest pageRequest = new HostMonitorPageRequest();
        pageRequest.setPageNum(request.getPage());
        pageRequest.setPageSize(request.getPageSize());
        pageRequest.setAgentNames(request.getAgentNames());
        if (Objects.nonNull(request.getStartTime())) {
            pageRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));
            if (Objects.nonNull(request.getEndTime())) {
                pageRequest.setEndTime(TimeUtils.stringToLocalDateTime(request.getEndTime()));
            } else {
                pageRequest.setEndTime(LocalDateTime.now());
            }
        }
        var result = hostMonitorService.queryProcessesWithTimeRange(pageRequest).getSource();
        if (CollectionUtils.isEmpty(result.getObjs())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        List<ProcessBO> res = new ArrayList<>();
        result.getObjs().forEach(wazuhProcessModel -> {
            var process = new ProcessBO();
            BeanUtils.copyProperties(wazuhProcessModel, process);
            res.add(process);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(res, result.getTotal())).asResult();
    }

    @GetMapping("/host_info")
    @ApiOperation(value = "查询主机信息", notes = "")
    public ListObjServiceResult<HostBO> queryHostInfo(PageQueryRequest request) {
        var group = getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        processQueryRequest(request, group);
        HostMonitorPageRequest pageRequest = new HostMonitorPageRequest();
        pageRequest.setPageNum(request.getPage());
        pageRequest.setPageSize(request.getPageSize());
        pageRequest.setAgentNames(request.getAgentNames());
        if (Objects.nonNull(request.getStartTime())) {
            pageRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));
            if (Objects.nonNull(request.getEndTime())) {
                pageRequest.setEndTime(TimeUtils.stringToLocalDateTime(request.getEndTime()));
            } else {
                pageRequest.setEndTime(LocalDateTime.now());
            }
        }
        var result = hostMonitorService.queryHostStateEventsWithTimeRange(pageRequest).getSource();
        if (CollectionUtils.isEmpty(result.getObjs())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        List<HostBO> res = new ArrayList<>();
        result.getObjs().forEach(wazuhHostStateModel -> {
            var host = new HostBO();
            host.setAgentIp(wazuhHostStateModel.getAgentIp());
            host.setMemoryUsePercent(wazuhHostStateModel.getMemoryPercent());
            host.setDiskUsePercent(wazuhHostStateModel.getDiskPercent());
            host.setCpuPercent(wazuhHostStateModel.getCpuTotalPercent());
            host.setTime(wazuhHostStateModel.getTime());
            res.add(host);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(res, result.getTotal())).asResult();
    }

    @GetMapping("/port_info")
    @ApiOperation(value = "查询端口信息", notes = "")
    public ListObjServiceResult<Port> queryPortInfo(PageQueryRequest request) {
        var group = getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        processQueryRequest(request, group);
        CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
        collectorBaseRequest.setPage(request.getPage());
        collectorBaseRequest.setPageSize(request.getPageSize());
        collectorBaseRequest.setAgentName(request.getAgentNames().get(0));
        var result = hostMonitorService.pagePorts(collectorBaseRequest).asResult().getSource();
        if (CollectionUtils.isEmpty(result.getObjs())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        result.getObjs().forEach(port -> port.setAgentIp(request.getAgentIps().get(0)));
        return new ListObjServiceResult<>(result).asResult();
    }

    @GetMapping("/system_user")
    @ApiOperation(value = "查询用户信息", notes = "")
    public ListObjServiceResult<SystemUserBO> querySystemUser(PageQueryRequest request) {
        var group = getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        processQueryRequest(request, group);
        HostMonitorPageRequest pageRequest = new HostMonitorPageRequest();
        pageRequest.setPageNum(request.getPage());
        pageRequest.setPageSize(request.getPageSize());
        pageRequest.setAgentNames(request.getAgentNames());
        if (Objects.nonNull(request.getStartTime())) {
            pageRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));
            if (Objects.nonNull(request.getEndTime())) {
                pageRequest.setEndTime(TimeUtils.stringToLocalDateTime(request.getEndTime()));
            } else {
                pageRequest.setEndTime(LocalDateTime.now());
            }
        }
        var result = hostMonitorService.querySystemUserWithTimeRange(pageRequest).getSource();
        if (CollectionUtils.isEmpty(result.getObjs())) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<>(), 0));
        }
        List<SystemUserBO> res = new ArrayList<>();
        result.getObjs().forEach(wazuhSystemUserModel -> {
            var systemUser = new SystemUserBO();
            BeanUtils.copyProperties(wazuhSystemUserModel, systemUser);
            res.add(systemUser);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(res, result.getTotal())).asResult();
    }

    private GroupConfig.Group getGroup(String groupId) {
        if (groupConfig == null) {
            groupConfig = BeanCommon.getBean(GroupConfig.class);
        }
        var groups = groupConfig.getGroups().stream()
                .filter(g -> g.getGroupId().equals(groupId))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(groups)) {
            return groups.get(0);
        }
        return null;
    }

    private void processQueryRequest(PageQueryRequest request, GroupConfig.Group group) {
        request.ensure();
        if (CollectionUtils.isEmpty(group.getNodes()) || CollectionUtils.isEmpty(request.getAgentIps())) {
            return;
        }
        group.getNodes().forEach(node -> {
            if (request.getAgentIps().contains(node.getNodeIp())) {
                request.getAgentNames().add(node.getNodeName());
            }
        });
    }
}
