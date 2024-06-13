package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.CloudClient;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.InstanceBatchDeleteData;
import cn.cyberpeace.mp.devopscluster.cloud.platform.params.VirtualNodePageParam;
import cn.cyberpeace.mp.devopscluster.cloud.platform.result.VirtualNode;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.VmService;
import cn.cyberpeace.mp.devopscluster.converter.bo.InstanceBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.InstancePageDTO;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.common.InstanceModel;
import cn.cyberpeace.mp.devopscluster.request.VirtualNodeDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.VirtualNodePageRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 虚拟机管理
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class VmConverter implements BaseConverter{

    private final VmService vmService;
    private final CloudClient client;
    private final CsceneSpecialService csceneSpecialService;


    public ListObjServiceResult<VirtualNode> page(VirtualNodePageRequest request) {
        VirtualNodePageParam param = new VirtualNodePageParam();
        param.setName(request.getName());
        param.setNodeType(request.getNodeType());
        param.setPage(request.getPage());
        param.setPerPage(request.getPerPage());
        param.setIsAll(request.getIsAll());
        var result = client.virtualNodePage(param);
        if (CollectionUtils.isEmpty(result.getObjs())) {
            return new ListObjServiceResult<>(result);
        }
        Map<String, String> name2ElementId = new HashMap<>();
        List<String> elementIds = result.getObjs().stream().map(virtualNode -> {
            String elementId;
            if (virtualNode.getName().startsWith("MP_SCENE")) {
                elementId = virtualNode.getName().replace("MP_SCENE_", "");
            } else if (virtualNode.getName().startsWith("CONFIG_")) {
                elementId = StringUtils.substringBeforeLast(virtualNode.getName(), "_").replace("CONFIG_", "");
            } else if (virtualNode.getName().startsWith("TREX_")) {
                elementId = virtualNode.getName().replace("TREX_", "");
                elementId = StringUtils.substringBeforeLast(elementId, "_");
            } else {
                elementId = virtualNode.getName();
            }
            name2ElementId.put(virtualNode.getName(), elementId);
            return elementId;
        }).distinct().collect(Collectors.toList());
        var elementId2Scene = csceneSpecialService.statDeviceReferScenes(elementIds).getSource();
        result.getObjs().forEach(virtualNode -> {
            var elementId = name2ElementId.get(virtualNode.getName());
            var scene = elementId2Scene.get(elementId);
            if (Objects.nonNull(scene)) {
                virtualNode.setSceneName(scene.getName());
            } else {
                virtualNode.setSceneName("-");
            }
        });
        return new ListObjServiceResult<>(result);
    }



    /**
     * 分页查询虚拟机信息
     * @param dto
     * @param ignoreFields 不需要传递给云平台接口的参数,默认过滤page与perPage,此参数已转化成limit与offset这两个参数
     * @return
     */
    public BasePageBO<InstanceBO> page(InstancePageDTO dto, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = ParamUtils.wrapperGetParamsByObject(dto,ignoreFields);
        CloudPlatformPageResult<InstanceModel> model = vmService.page(getParams);
        List<InstanceBO> list = model.getList().stream().map(instanceModel ->
                InstanceBO.builder().name(instanceModel.getName()).node(instanceModel.getHost())
                        .runningTime(instanceModel.getRunningTime())
                        .addressList(Optional.ofNullable(instanceModel.getAddresses()).orElse(Collections.emptyList())
                                .stream().map(InstanceModel.Address::getIp)
                                .collect(Collectors.toList()))
                        .status(instanceModel.getStatus()).createTime(instanceModel.getCreatedAt()).build()
        ).collect(Collectors.toList());
        BasePageBO<InstanceBO> convertModel = new BasePageBO<>();
        convertModel.setList(list);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    /**
     * 获取虚拟机总数
     * @param dto
     * @return
     */
    public Integer getTotalInstances(InstancePageDTO dto) {
        dto.setLimit(NumberUtils.INTEGER_ONE);
        BasePageBO<InstanceBO> page = this.page(dto,this.getTotalPageIgnoreField());
        return page.getTotal() ;
    }

    public ServiceResult<Boolean> batchDelete(VirtualNodeDeleteRequest request) {
        InstanceBatchDeleteData deleteData = new InstanceBatchDeleteData();
        deleteData.setUuids(request.getInstances());
        client.batchDeleteInstance(deleteData);
        return new ServiceResult<>(true);
    }
}
