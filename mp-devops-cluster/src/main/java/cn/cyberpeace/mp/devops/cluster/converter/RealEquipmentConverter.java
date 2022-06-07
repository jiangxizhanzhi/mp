package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.RealEquipmentService;
import cn.cyberpeace.mp.devops.cluster.converter.bo.RealEquipmentBO;
import cn.cyberpeace.mp.devops.cluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devops.cluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetAuthService;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实装管理
 * @author wanggang
 */
@Service
@RequiredArgsConstructor
public class RealEquipmentConverter implements BaseConverter{

    private final RealEquipmentService realEquipmentService;

    private final DeviceService deviceService;

    private final DevopsTargetService devopsTargetService;

    private final DevopsTargetAuthService devopsTargetAuthService;

    private final TargetConverter targetConverter;

    /**
     * 获取实装总数
     * @param request
     * @return
     */
    public Integer getTotalRealEquipment(RealEquipmentPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        CloudPlatformPageResult<Device> page = realEquipmentService.page(getParams);
        return page.getTotal();
    }

    /**
     * 分页查询实装列表
     * @param dto
     * @return
     */
    public BasePageBO<RealEquipmentBO> page(ImagePageDTO dto) {
        Integer total = devopsTargetService.totalCount(dto);

        BasePageBO<RealEquipmentBO> convertModel = new BasePageBO<>();
        if (total == 0){
            convertModel.setTotal(0);
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = devopsTargetService.page(dto);

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            convertModel.setTotal(0);
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            convertModel.setTotal(0);
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }

        Map<String, DeviceTargetPO> map = deviceTargetPOS.stream().collect(Collectors.toMap(DeviceTargetPO::getUuid, Function.identity()));
        List<RealEquipmentBO> modelList = model.getList().stream().map(device -> {
            RealEquipmentBO realEquipmentBO = convertDevice(device);
            DeviceTargetPO deviceTargetPO = map.get(device.getUuid());
            if (Objects.nonNull(deviceTargetPO)){
                realEquipmentBO.setReferenceStatus(deviceTargetPO.getReferenceStatus());
                realEquipmentBO.setUseStatus(deviceTargetPO.getUseStatus());
            }
            return realEquipmentBO;
        }).collect(Collectors.toList());

        convertModel.setList(modelList);
        convertModel.setTotal(total);
        return convertModel;
    }


    /**
     * 根据ID查询实装信息
     * @param id
     * @return
     */
    public RealEquipmentBO findById(String id) {
        Device device = deviceService.findById(id);
        return convertDevice(device);
    }

    /**
     * 新增实装设备
     * @param request
     * @return
     */
    @Transactional
    public Device add(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        if (Objects.isNull(device)){
            return null;
        }
        Device result = realEquipmentService.add(device);
        if (Objects.isNull(result) || !result.isSuccess()){
            return result;
        }
        devopsTargetService.insert(Arrays.asList(result));
        return result;
    }

    /**
     * 更新实装设备信息
     * @param request
     * @return
     */
    @Transactional
    public Device update(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        Device result = realEquipmentService.update(request.getUuid(),device);
        devopsTargetService.updateById(result);
        return result;
    }

    /**
     * 批量删除实装设备信息
     * @param request
     * @return
     */
    @Transactional
    public BaseCloudPlatformResult delete(RealEquipmentDeleteRequest request) {
        targetConverter.validTargetCanDelete(request.getUuids());
        BaseCloudPlatformResult result = deviceService.deleteByIds(request.getUuids());
        if (result.isSuccess()) {
            ImageDeleteDTO dto = new ImageDeleteDTO();
            dto.setIds(request.getUuids());
            devopsTargetService.delete(dto);
            devopsTargetAuthService.deleteByTargets(request.getUuids());
        }
        return result;
    }

    /**
     * 转换Device对象
     * @param device
     * @return RealEquipment
     */
    protected RealEquipmentBO convertDevice(Device device){
        if (Objects.isNull(device)){
            return new RealEquipmentBO();
        }

        RealEquipmentBO convertModel = RealEquipmentBO.builder().uuid(device.getUuid())
                .name(device.getName()).role(device.getRole()).logo(device.getLogo())
                .mainCategory(device.getCategory()).subCategory(device.getSecondCategory())
                .description(device.getDescription()).thirdCategory(device.getL3Category()).extEntityAccess(device.getExtEntityAccess()).build();
        convertModel.setSuccess(device.isSuccess());
        convertModel.setMessage(device.getErrorMessage());
        convertModel.setExtEntity(device.getExtEntity());
        convertModel.setExtSystemInfo(device.getExtSystemInfo());
        convertModel.setExtGateway(device.getExtGateway());
        ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)){
            convertModel.setInPosition(extEntity.getEntityStatus() ? StatusEnum.YES : StatusEnum.NO);
        }
        return convertModel;
    }

    /**
     * 转换Request对象
     * @param request
     * @return
     */
    protected Device convertRequest(RealEquipmentRequest request){
        Device device = new Device();
        device.setName(request.getName());
        device.setDescription(request.getDescription());
        device.setLogo(request.getLogo());
        device.setRole(request.getRole());
        device.setCategory(request.getMainCategory());
        device.setSecondCategory(request.getSubCategory());
        device.setL3Category(Objects.nonNull(request.getThirdCategory()) ? request.getThirdCategory() : 0);
        device.setExtEntityAccess(request.getExtEntityAccess());
        device.setExtEntity(request.getExtEntity());
        device.setExtSystemInfo(request.getSystemInfo());
        return device;
    }
}
