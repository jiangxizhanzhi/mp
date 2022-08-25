package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.basic.TimeUtils;
import cn.cyberpeace.mp.collection.model.FileEventModel;
import cn.cyberpeace.mp.collection.model.wazuh.common.request.CollectorBaseRequest;
import cn.cyberpeace.mp.collection.model.wazuh.syscollector.Port;
import cn.cyberpeace.mp.collection.request.BaseEventRequest;
import cn.cyberpeace.mp.collection.request.HostMonitorPageRequest;
import cn.cyberpeace.mp.collection.request.SyscheckEventRequest;
import cn.cyberpeace.mp.collection.service.DataCollectService;
import cn.cyberpeace.mp.collection.service.HostMonitorService;
import cn.cyberpeace.mp.collectionapi.config.GroupConfig;
import cn.cyberpeace.mp.collectionapi.request.PageQueryRequest;
import cn.cyberpeace.mp.collectionapi.util.GroupUtil;
import cn.cyberpeace.mp.collectionapi.vo.HostVO;
import cn.cyberpeace.mp.collectionapi.vo.ProcessVO;
import cn.cyberpeace.mp.collectionapi.vo.SystemUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wazuh")
@Api(value = "态势感知-事件查询接口", tags = "态势感知-事件查询接口")
public class DataCollectionController {
    @Resource(name = "dataCollectServiceImpl")
    private DataCollectService dataCollectService;

    @Resource(name = "hostMonitorServiceImpl")
    private HostMonitorService hostMonitorService;

    @GetMapping("/file_events")
    @ApiOperation(value = "查询文件变更事件", notes = "")
    public ListObjServiceResult<FileEventModel> queryFileEvents(PageQueryRequest request) {
        var group = GroupUtil.getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<FileEventModel>(), 0)).asResult();
        }
        var name2Ip = processQueryRequest(request, group);
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
        var result = dataCollectService.queryFileModifyEvents(eventRequest);
        if (result.getSource() == null || CollectionUtils.isEmpty(result.getSource().getObjs())) {
            return result.asResult();
        }
        //  处理linux靶机里的容器ip
        result.getSource().getObjs().forEach(fileEventModel -> fileEventModel.setAgentIp(name2Ip.get(fileEventModel.getAgentName())));
        return result.asResult();
    }

    @GetMapping("/process_info")
    @ApiOperation(value = "查询进程信息", notes = "")
    public ListObjServiceResult<ProcessVO> queryProcessInfo(PageQueryRequest request) {
        var group = GroupUtil.getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<ProcessVO>(), 0)).asResult();
        }
        var name2Ip = processQueryRequest(request, group);
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
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<ProcessVO>(), 0)).asResult();
        }
        List<ProcessVO> res = new ArrayList<>();
        result.getObjs().forEach(wazuhProcessModel -> {
            var process = new ProcessVO();
            BeanUtils.copyProperties(wazuhProcessModel, process);
            //  处理linux靶机里的容器ip
            process.setAgentIp(name2Ip.get(wazuhProcessModel.getAgentName()));
            res.add(process);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(res, result.getTotal())).asResult();
    }

    @GetMapping("/host_info")
    @ApiOperation(value = "查询主机信息", notes = "")
    public ListObjServiceResult<HostVO> queryHostInfo(PageQueryRequest request) {
        var group = GroupUtil.getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<HostVO>(), 0)).asResult();
        }
        var name2Ip = processQueryRequest(request, group);
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
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<HostVO>(), 0)).asResult();
        }
        List<HostVO> res = new ArrayList<>();
        result.getObjs().forEach(wazuhHostStateModel -> {
            var host = new HostVO();
            //  处理linux靶机里的容器ip
            host.setAgentIp(name2Ip.get(wazuhHostStateModel.getAgentName()));
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
        var group = GroupUtil.getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<Port>(), 0)).asResult();
        }
        var name2Ip = processQueryRequest(request, group);
        List<Port> res = new ArrayList<>();
        for (String a : request.getAgentNames()) {
            CollectorBaseRequest collectorBaseRequest = new CollectorBaseRequest();
            //  查询所有端口数据
            collectorBaseRequest.setPage(1);
            collectorBaseRequest.setPageSize(10000);
            collectorBaseRequest.setAgentName(a);
            var result = hostMonitorService.pagePorts(collectorBaseRequest).asResult().getSource();
            if (CollectionUtils.isEmpty(result.getObjs())) {
                continue;
            }
            result.getObjs().forEach(port -> port.setAgentIp(name2Ip.get(a)));
            res.addAll(result.getObjs());
        }
        int offset = (request.getPage() - 1) * request.getPageSize();
        int total = res.size();
        res = res.stream().skip(offset).limit(request.getPageSize()).collect(Collectors.toList());
        return new ListObjServiceResult<>(new ListObjResult<>(res, total)).asResult();
    }

    @GetMapping("/system_user")
    @ApiOperation(value = "查询用户信息", notes = "")
    public ListObjServiceResult<SystemUserVO> querySystemUser(PageQueryRequest request) {
        var group = GroupUtil.getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<SystemUserVO>(), 0)).asResult();
        }
        var name2Ip = processQueryRequest(request, group);
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
            return new ListObjServiceResult<>(new ListObjResult<>(new ArrayList<SystemUserVO>(), 0)).asResult();
        }
        List<SystemUserVO> res = new ArrayList<>();
        result.getObjs().forEach(wazuhSystemUserModel -> {
            var systemUser = new SystemUserVO();
            BeanUtils.copyProperties(wazuhSystemUserModel, systemUser);
            systemUser.setAgentIp(name2Ip.get(wazuhSystemUserModel.getAgentName()));
            res.add(systemUser);
        });
        return new ListObjServiceResult<>(new ListObjResult<>(res, result.getTotal())).asResult();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "删除es数据", notes = "")
    public ServiceResult<Long> deleteEsData(PageQueryRequest request) {
        var group = GroupUtil.getGroup(request.getTeamId());
        if (Objects.isNull(group)) {
            return new ServiceResult<>(0L);
        }
        processQueryRequest(request, group);
        BaseEventRequest baseEventRequest = new BaseEventRequest();
        baseEventRequest.setAgentNames(request.getAgentNames());
        if (Objects.nonNull(request.getStartTime())) {
            baseEventRequest.setStartTime(TimeUtils.stringToLocalDateTime(request.getStartTime()));
            if (Objects.nonNull(request.getEndTime())) {
                baseEventRequest.setEndTime(TimeUtils.stringToLocalDateTime(request.getEndTime()));
            } else {
                baseEventRequest.setEndTime(LocalDateTime.now());
            }
        }
        return dataCollectService.deleteDocByAgentNames(baseEventRequest);
    }

    private Map<String, String> processQueryRequest(PageQueryRequest request, GroupConfig.Group group) {
        request.ensure();
        Map<String, String> name2Ip = new HashMap<>();
        if (CollectionUtils.isEmpty(group.getNodes()) || CollectionUtils.isEmpty(request.getAgentIps())) {
            return name2Ip;
        }
        group.getNodes().forEach(node -> {
            if (request.getAgentIps().contains(node.getNodeIp())) {
                request.getAgentNames().addAll(node.getNodeNames());
                node.getNodeNames().forEach(n -> name2Ip.put(n, node.getNodeIp()));
            }
        });
        return name2Ip;
    }
}
