package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.common.BeanCommon;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneNodeInfoBO;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.VmConverter;
import cn.cyberpeace.mp.devopscluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.request.InstancePageRequest;
import cn.cyberpeace.mp.devopscluster.request.ReviseContainerOrVmRequest;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import cn.cyberpeace.mp.devopscluster.vo.InstancePageVO;
import cn.cyberpeace.mp.devopscluster.vo.common.PageResult;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.VirtualMachine;
import cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param.VirtualMachineGetResult;
import cn.cyberpeace.mp.situationaware.service.DataCollectionService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 虚拟机管理类
 */
@Validated
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/instance")
@Api(tags = "计算实例管理-虚拟机")
@RequiredArgsConstructor
public class VmController {

    private final VmConverter vmConverter;

    private final CsceneSpecialService csceneSpecialService;

    private final UserService userService;

    private final DeviceTargetMapper deviceTargetMapper;

    /**
     * 重启
     * @param request
     * @return
     */
    @ApiOperation("重启虚拟机")
    @PostMapping("/restart")
    public ServiceResult<Boolean> restart(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.restart(request.getId())).asResult();
    }

    /**
     * 暂停
     * @param request
     * @return
     */
    @ApiOperation("暂停虚拟机")
    @PostMapping("/pause")
    public ServiceResult<Boolean> pause(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.pause(request.getId())).asResult();
    }

    /**
     * 恢复
     * @param request
     * @return
     */
    @ApiOperation("恢复虚拟机")
    @PostMapping("/unpause")
    public ServiceResult<Boolean> unpause(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.unpause(request.getId())).asResult();
    }

    /**
     * 开启
     * @param request
     * @return
     */
    @ApiOperation("开启虚拟机")
    @PostMapping("/start")
    public ServiceResult<Boolean> start(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.start(request.getId())).asResult();
    }

    /**
     * 关闭
     * @param request
     * @return
     */
    @ApiOperation("关闭虚拟机")
    @PostMapping("/stop")
    public ServiceResult<Boolean> stop(@RequestBody ReviseContainerOrVmRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        VirtualMachine virtualMachine = new VirtualMachine();
        return new ServiceResult<>(virtualMachine.stop(request.getId())).asResult();
    }

    /**
     * 分页查询虚拟机信息
     * @param request
     * @return
     */
    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/page")
    public PageResult<InstancePageVO> page(InstancePageRequest request) {
        InstancePageDTO dto = new InstancePageDTO();
        dto.setOffset(1);
        dto.setLimit(1000);
        // host 不需要在底层筛选，直接使用下面在内存中模糊搜索
        dto.setHost(null);
        BasePageBO<InstanceBO> model = vmConverter.page(dto);
        List<InstancePageVO> list = convertBOToPageVO(model);

        List<InstancePageVO> filterRes = new ArrayList<>();
        for (var vo : list) {
            if (StringUtils.isNotEmpty(request.getUserName())) {
                if (vo.getUserName() == null || !vo.getUserName().contains(request.getUserName())) {
                    continue;
                }
            }
            if (StringUtils.isNotEmpty(request.getDeviceName())) {
                if (vo.getSceneNodeName() == null || !vo.getSceneNodeName().contains(request.getDeviceName())) {
                    continue;
                }
            }
            if (StringUtils.isNotEmpty(request.getStatus())) {
                if (vo.getStatus() == null || !vo.getStatus().equals(request.getStatus())) {
                    continue;
                }
            }
            if (StringUtils.isNotEmpty(request.getHost())) {
                if (vo.getNode() == null || !vo.getNode().contains(request.getHost())) {
                    continue;
                }
            }
            filterRes.add(vo);
        }

        int start = (request.getPage() - 1) * request.getPerPage();
        if (start >= filterRes.size()) {
            return new PageResult<>(new ArrayList<>(), filterRes.size());
        }
        int end = Math.min(start + request.getPerPage(), filterRes.size());
        var res = filterRes.subList(start, end);

        convertBOToPageVOV2(res);
        // 场景中的节点使用节点名称作为name
        for (var vo : res) {
            if (StringUtils.isNotEmpty(vo.getSceneNodeName())) {
                vo.setName(vo.getSceneNodeName());
            }
        }

        return new PageResult<>(res, filterRes.size());
    }

    @ApiOperation("分页查询虚拟机信息")
    @GetMapping("/status_info")
    public ServiceResult<List<StatusInfo>> getStatusInfo() {
        List<StatusInfo> result = new ArrayList<>();

        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setName("已启动");
        statusInfo.setValue(VirtualMachineGetResult.Status.ACTIVE);
        result.add(statusInfo);

        statusInfo = new StatusInfo();
        statusInfo.setName("未启动");
        statusInfo.setValue(VirtualMachineGetResult.Status.SHUTOFF);
        result.add(statusInfo);

        statusInfo = new StatusInfo();
        statusInfo.setName("暂停");
        statusInfo.setValue(VirtualMachineGetResult.Status.PAUSED);
        result.add(statusInfo);

        return new ServiceResult<>(result).asResult();
    }

    @Getter
    @Setter
    public static class StatusInfo {
        private String name;
        private String value;
    }

    /**
     * 转换BO to VO
     * @param model
     * @return
     */
    private List<InstancePageVO> convertBOToPageVO(BasePageBO<InstanceBO> model) {
        List<InstanceBO> instances = model.getList();
        List<String> instanceNames = instances.stream().map(InstanceBO::getName).collect(Collectors.toList());
        List<UserBo> userBoList = new ArrayList<>();
        Map<String, String> uuid2UserId = new HashMap<>();
        ServiceResult<Map<String, CsceneNodeInfoBO>> serviceResult = csceneSpecialService.getSceneInfoByNodeResourceName(instanceNames);
        var devopsResult = deviceTargetMapper.findByUuids(instanceNames);
        POLogoUtils.changeLogoPathToUrl(devopsResult);
        Map<String, CsceneNodeInfoBO> source = serviceResult.getSource();
        if (Objects.nonNull(source)) {
            List<CsceneNodeInfoBO> values = new ArrayList<>(source.values());
            if (CollectionUtils.isNotEmpty(values)) {
                List<String> userIdList = values.stream().map(CsceneNodeInfoBO::getUserId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(userIdList)) {
                    var list = userService.listByUserIds(userIdList.toArray(new String[]{}));
                    if (CollectionUtils.isNotEmpty(list)) {
                        userBoList = list;
                    }
                }
            }
        }
        // 镜像管理实例的创建用户
        if (CollectionUtils.isNotEmpty(devopsResult)) {
            List<String> userIdList = devopsResult.stream()
                    .filter(deviceTargetPO -> StringUtils.isNotEmpty(deviceTargetPO.getInstanceCreateUser()))
                    .map(DeviceTargetPO::getInstanceCreateUser).collect(Collectors.toList());
            uuid2UserId = devopsResult.stream()
                    .filter(deviceTargetPO -> StringUtils.isNotEmpty(deviceTargetPO.getInstanceCreateUser()))
                    .collect(Collectors.toMap(DeviceTargetPO::getUuid, DeviceTargetPO::getInstanceCreateUser));
            if (CollectionUtils.isNotEmpty(userIdList)) {
                userBoList.addAll(userService.listByUserIds(userIdList.toArray(new String[]{})));
            }
        }
        List<UserBo> tmpUserBoList = userBoList;
        Map<String, String> uuid2User = uuid2UserId;

        return instances.stream().map(instanceModel -> {
            CsceneNodeInfoBO csceneNodeInfoBO = new CsceneNodeInfoBO();
            if (Objects.nonNull(source.get(instanceModel.getName()))) {
                csceneNodeInfoBO = source.get(instanceModel.getName());
            }
            String userName = "";
            String userId = csceneNodeInfoBO.getUserId();
            if (StringUtils.isNotBlank(userId)) {
                Optional<UserBo> optional = tmpUserBoList.stream().filter(userDto -> userDto.getId().equals(userId))
                        .findFirst();
                if (optional.isPresent()) {
                    userName = optional.get().getUsername();
                }
            } else if (StringUtils.isNotEmpty(uuid2User.get(instanceModel.getName()))) {
                String createUser = uuid2User.get(instanceModel.getName());
                Optional<UserBo> optional = tmpUserBoList.stream().filter(userDto -> userDto.getId().equals(createUser))
                        .findFirst();
                if (optional.isPresent()) {
                    userName = optional.get().getUsername();
                }
            }

            var r1 = InstancePageVO.builder().name(instanceModel.getName())
                    .sceneNodeName(csceneNodeInfoBO.getNodeName())
                    .nodeEid(csceneNodeInfoBO.getInternalNodeEid())
                    .sceneName(StringUtils.isBlank(csceneNodeInfoBO.getCsceneName()) ? "" : csceneNodeInfoBO.getCsceneName())
                    .id(instanceModel.getId()).node(instanceModel.getNode()).addressList(instanceModel.getAddressList())
                    .userName(userName).ram(csceneNodeInfoBO.getRam())
                    .referenceType(csceneNodeInfoBO.getReferenceType()).event(csceneNodeInfoBO.getEvent())
                    .eventStatus(csceneNodeInfoBO.getEventStatus()).sceneStatus(csceneNodeInfoBO.getSceneStatus())
                    .sceneStatusDesc(csceneNodeInfoBO.getSceneStatusDesc()).runningTime(instanceModel.getRunningTime())
                    .processMessage(csceneNodeInfoBO.getProcessMessage())
                    .cpuUsePercentage(csceneNodeInfoBO.getCpuPercent()).status(instanceModel.getStatus())
                    .createTime(instanceModel.getCreateTime()).build();
            // 暂停结束、挂起结束 的状态时，其最后运行时间为 节点的update time；否则为当前时间
            if (csceneNodeInfoBO.isPause() || csceneNodeInfoBO.isSuspend()) {
                r1.setLastRunTime(csceneNodeInfoBO.getNodeUpdateTime());
            } else {
                r1.setLastRunTime(LocalDateTime.now());
            }
            return r1;
        }).collect(Collectors.toList());
    }

    private void convertBOToPageVOV2(List<InstancePageVO> list) {
        List<String> agentNames = new ArrayList<>();
        Map<String, InstancePageVO> source = new HashMap<>();
        for (InstancePageVO l : list) {
            if (StringUtils.isNotEmpty(l.getNodeEid())) {
                agentNames.add(l.getNodeEid());
                source.put(l.getNodeEid(), l);
            }
        }

        if (CollectionUtils.isNotEmpty(agentNames)) {
            // 批量查询探针采集的cpu使用率
            RestHighLevelClient client = (RestHighLevelClient) BeanCommon.getBean("logRestHighLevelClient");
            LocalDateTime now = LocalDateTime.now();
            var indexArray = DataCollectionService.buildRequestIndex(now.minusDays(1), now);
            var searchRequest = new SearchRequest(indexArray);
            var boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.must(QueryBuilders.termQuery("data.sic_launch_id", "eda72998ba654c8d8625f61eaa918eb3"))
                    .must(QueryBuilders.termsQuery("agent.name", agentNames.toArray(new String[]{})));

            var sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.sort("timestamp", SortOrder.DESC);
            sourceBuilder.query(boolQueryBuilder);
            sourceBuilder.size(agentNames.size() * 3);
            searchRequest.source(sourceBuilder);
            try {
                SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
                long totalHits = response.getHits().getTotalHits().value;
                if (totalHits > 0) {
                    for (SearchHit hit : response.getHits().getHits()) {
                        EsResponseData data = JSONObject.parseObject(hit.getSourceAsString(), EsResponseData.class);
                        var percent = data.getData().getSicLaunchOutput().getCPUPercent();
                        var nodeEid = data.getAgent().getName();
                        String key = nodeEid;
                        if (source.containsKey(key) && percent != null) {
                            source.get(key).setCpuUsePercentage(percent);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("failed to query elasticsearch", e);
            }
        }
    }

    @Getter
    @Setter
    private static class EsResponseData{
        private Agent agent ;
        private Data data ;
    }

    @Getter
    @Setter
    private static class Agent {
        private String name;
    }

    @Getter
    @Setter
    private static class Data {
        @JSONField(name = "sic_launch_output")
        private Sic sicLaunchOutput;
    }

    @Getter
    @Setter
    private static class Sic {
        @JSONField(name = "CPUPercent")
        private Double CPUPercent;
    }
}
