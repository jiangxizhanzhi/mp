package cn.cyberpeace.mp.devops.cluster.converter;

import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.RealEquipmentService;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.BaseCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.CloudPlatformPageModel;
import cn.cyberpeace.mp.devops.cluster.po.TargetPO;
import cn.cyberpeace.mp.devops.cluster.repository.ImageAuthMapper;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
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

    private final ImageMapper imageMapper;

    private final ImageAuthMapper imageAuthMapper;

    private final TargetConverter targetConverter;

    /**
     * 获取实装总数
     * @param request
     * @return
     */
    public Integer getTotalRealEquipment(RealEquipmentPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        CloudPlatformPageModel<Device> page = realEquipmentService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }

    /**
     * 分页查询实装列表
     * @param request
     * @return
     */
    public RealEquipmentConvertModel page(RealEquipmentPageRequest request) {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setMainCategory(request.getCategory());
        dto.setSubCategory(request.getSecondCategory());
        dto.setThirdCategory(request.getThirdCategory());
        dto.setImageType(ImageTypeEnum.ENTITY.toValue());
        dto.setUseStatus(request.getUseStatus());
        dto.setName(request.getName());
        dto.setInPosition(request.getInPosition());
        dto.setOffset((request.getPage() - 1) * request.getPerPage());
        dto.setLimit(request.getPerPage());
        dto.setIsHide(Boolean.FALSE);
        Integer total = imageMapper.totalCount(dto);

        RealEquipmentConvertModel convertModel = new RealEquipmentConvertModel();
        if (total == 0){
            convertModel.setTotal(0);
            convertModel.setModelList(Lists.newArrayList());
            return convertModel;
        }
        List<TargetPO> targetPOS = imageMapper.page(dto);

        List<Integer> ids = targetPOS.stream().map(TargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            convertModel.setTotal(0);
            convertModel.setModelList(Lists.newArrayList());
            return convertModel;
        }
        CloudPlatformPageModel<Device> model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            convertModel.setTotal(0);
            convertModel.setModelList(Lists.newArrayList());
            return convertModel;
        }

        Map<String, TargetPO> map = targetPOS.stream().collect(Collectors.toMap(TargetPO::getUuid, Function.identity()));
        List<RealEquipment> modelList = model.getList().stream().map(device -> {
            RealEquipment realEquipment = convertDevice(device);
            TargetPO targetPO = map.get(device.getUuid());
            if (Objects.nonNull(targetPO)){
                realEquipment.setReferenceStatus(targetPO.getReferenceStatus());
                realEquipment.setUseStatus(targetPO.getUseStatus());
            }
            return realEquipment;
        }).collect(Collectors.toList());

        convertModel.setModelList(modelList);
        convertModel.setTotal(total);
        return convertModel;
    }


    /**
     * 根据ID查询实装信息
     * @param id
     * @return
     */
    public RealEquipment findById(String id) {
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
    public BaseCloudPlatformModel delete(RealEquipmentDeleteRequest request) {
        targetConverter.validTargetCanDelete(request.getUuids());
        BaseCloudPlatformModel result = deviceService.deleteByIds(request.getUuids());
        if (result.isSuccess()) {
            ImageDeleteDTO dto = new ImageDeleteDTO();
            dto.setIds(request.getUuids());
            imageMapper.delete(dto);
            request.getUuids().forEach(id -> imageAuthMapper.deleteByTarget(id));
        }
        return result;
    }

    /**
     * 转换Device对象
     * @param device
     * @return RealEquipment
     */
    protected RealEquipment convertDevice(Device device){
        if (Objects.isNull(device)){
            return new RealEquipment();
        }

        RealEquipment convertModel = RealEquipment.builder().uuid(device.getUuid())
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
