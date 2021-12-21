package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.service.RealEquipmentService;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.repository.ImageAuthMapper;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RealEquipmentConverterImpl implements RealEquipmentConverter {

    private RealEquipmentService realEquipmentService;

    private DeviceService deviceService;

    private DevopsTargetService devopsTargetService;

    private ImageMapper imageMapper;

    private ImageAuthMapper imageAuthMapper;

    private TargetConverter targetConverter;

    @Autowired
    public RealEquipmentConverterImpl(RealEquipmentService realEquipmentService, DeviceService deviceService,
                                      DevopsTargetService devopsTargetService, ImageMapper imageMapper,
                                      ImageAuthMapper imageAuthMapper,TargetConverter targetConverter) {
        this.realEquipmentService = realEquipmentService;
        this.deviceService = deviceService;
        this.devopsTargetService = devopsTargetService;
        this.imageMapper = imageMapper;
        this.imageAuthMapper = imageAuthMapper;
        this.targetConverter = targetConverter;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(RealEquipmentPageRequest::getPage,RealEquipmentPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldNames(RealEquipmentPageRequest::getOffset,
                RealEquipmentPageRequest::getPage,RealEquipmentPageRequest::getPerPage);
    }

    @Override
    public Integer getTotalRealEquipment(RealEquipmentPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        DeviceCloudPlatformModel page = realEquipmentService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }

    @Override
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

        Integer total = imageMapper.totalCount(dto);

        RealEquipmentConvertModel convertModel = new RealEquipmentConvertModel();
        if (total == 0){
            convertModel.setTotal(0);
            convertModel.setModelList(Lists.newArrayList());
            return convertModel;
        }
        List<TargetModel> targetModels = imageMapper.page(dto);

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            convertModel.setTotal(0);
            convertModel.setModelList(Lists.newArrayList());
            return convertModel;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            convertModel.setTotal(0);
            convertModel.setModelList(Lists.newArrayList());
            return convertModel;
        }

        Map<String, TargetModel> map = targetModels.stream().collect(Collectors.toMap(TargetModel::getUuid, Function.identity()));
        List<RealEquipment> modelList = model.getItems().stream().map(device -> {
            RealEquipment realEquipment = convertDevice(device);
            TargetModel targetModel = map.get(device.getUuid());
            if (Objects.nonNull(targetModel)){
                realEquipment.setReferenceStatus(targetModel.getReferenceStatus());
                realEquipment.setUseStatus(targetModel.getUseStatus());
            }
            return realEquipment;
        }).collect(Collectors.toList());

        convertModel.setModelList(modelList);
        convertModel.setTotal(total);
        return convertModel;
    }


    @Override
    public RealEquipment findById(String id) {
        Device device = deviceService.findById(id);
        return convertDevice(device);
    }

    @Transactional
    @Override
    public Device add(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        if (Objects.isNull(device)){
            return null;
        }
        Device result = realEquipmentService.add(device);
        devopsTargetService.insert(Arrays.asList(result));
        return result;
    }

    @Transactional
    @Override
    public Device update(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        Device result = realEquipmentService.update(request.getUuid(),device);
        devopsTargetService.updateById(result);
        return result;
    }

    @Override
    @Transactional
    public Boolean delete(RealEquipmentDeleteRequest request) {
        targetConverter.validTargetCanDelete(request.getUuids());
        Boolean result = deviceService.deleteByIds(request.getUuids());
        if (result) {
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
        device.setL3Category(request.getThirdCategory());
        device.setExtEntityAccess(request.getExtEntityAccess());
        device.setExtEntity(request.getExtEntity());
        device.setExtSystemInfo(request.getSystemInfo());
        return device;
    }

}
