package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import cn.cyberpeace.mp.devops.cluster.model.common.*;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devops.cluster.request.TargetPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import cn.cyberpeace.mp.devops.cluster.utils.JsonUtils;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public RealEquipmentConverterImpl(RealEquipmentService realEquipmentService, DeviceService deviceService,
                                      DevopsTargetService devopsTargetService, ImageMapper imageMapper) {
        this.realEquipmentService = realEquipmentService;
        this.deviceService = deviceService;
        this.devopsTargetService = devopsTargetService;
        this.imageMapper = imageMapper;
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
        String getParams = BeanUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        DeviceCloudPlatformModel page = realEquipmentService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }

    @Override
    public RealEquipmentConvertModel page(RealEquipmentPageRequest request) {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setMainCategory(request.getCategory());
        dto.setSubCategory(request.getSecondCategory());
        dto.setImageType(ImageTypeEnum.ENTITY.toValue());
        dto.setUseStatus(request.getUseStatus());
        dto.setName(request.getName());
        dto.setInPosition(request.getInPosition());
        dto.setOffset((request.getPage() - 1) * request.getPerPage());
        dto.setLimit(request.getPerPage());

        Integer total = imageMapper.totalCount(dto);
        if (total == 0){
            return null;
        }
        List<TargetModel> targetModels = imageMapper.page(dto);

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return null;
        }

        Map<String, TargetModel> map = targetModels.stream().collect(Collectors.toMap(TargetModel::getUuid, Function.identity()));
        List<RealEquipment> modelList = model.getItems().stream().map(device -> {
            RealEquipment realEquipment = convertDevice(device);
            TargetModel targetModel = map.get(device.getUuid());
            if (Objects.nonNull(targetModel)){
               realEquipment.setInPosition(targetModel.getInPosition());
               realEquipment.setReferenceStatus(targetModel.getReferenceStatus());
               realEquipment.setUseStatus(targetModel.getUseStatus());
            }
            return realEquipment;
        }).collect(Collectors.toList());

        RealEquipmentConvertModel convertModel = new RealEquipmentConvertModel();
        convertModel.setModelList(modelList);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    @Override
    public List<Device> allTarget(TargetPageRequest request) {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setMainCategory(request.getCategory());
        dto.setSubCategory(request.getSecondCategory());
        dto.setImageType(ImageTypeEnum.ENTITY.toValue());
        dto.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
//        dto.setUseStatus(request.getUseStatus());
        dto.setName(request.getName());
//        dto.setInPosition(request.getInPosition());
//        dto.setOffset((request.getPage() - 1) * request.getPerPage());
//        dto.setLimit(request.getPerPage());

        Integer total = imageMapper.totalCount(dto);
        if (total == 0){
            return null;
        }
        List<TargetModel> targetModels = imageMapper.page(dto);

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return Lists.newArrayList();
        }

        return model.getItems();
    }

    @Override
    public RealEquipment findById(String id) {
        Device device = deviceService.findById(id);
        return convertDevice(device);
    }

    @Transactional
    @Override
    public RealEquipment add(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        if (Objects.isNull(device)){
            return new RealEquipment();
        }
        Device result = realEquipmentService.add(device);
        devopsTargetService.insert(result);
        return convertDevice(result);
    }

    @Override
    public RealEquipment update(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        Device result = realEquipmentService.update(request.getUuid(),device);
        devopsTargetService.updateById(result);
        return convertDevice(result);
    }

    @Override
    public Boolean delete(RealEquipmentDeleteRequest request) {
        return deviceService.deleteByIds(request.getUuids());
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
        final ExtEntity extEntity = device.getExtEntity();

        final ExtSystemInfo extSystemInfo = device.getExtSystemInfo();

        final ExtGateway extGateway = device.getExtGateway();

        RealEquipment convertModel = RealEquipment.builder().uuid(device.getUuid())
                .name(device.getName()).role(device.getRole()).logo(device.getLogo())
                .mainCategory(device.getCategory()).subCategory(device.getSecondCategory())
                .description(device.getDescription()).build();
        convertModel.setSuccess(device.isSuccess());
        if (Objects.nonNull(extEntity)){
            ExtEntity entity = new ExtEntity();
            entity.setStyle(extEntity.getStyle());
            entity.setParams(extEntity.getParams());
            entity.setAccessType(extEntity.getAccessType());
            entity.setEntityStatus(extEntity.getEntityStatus());
            entity.setAccessSwitchPort(extEntity.getAccessSwitchPort());
            entity.setAccessSwitchPortName(extEntity.getAccessSwitchPortName());
            entity.setAccessIpAddress(extEntity.getAccessIpAddress());
            entity.setAccessMacAddress(extEntity.getAccessMacAddress());
            entity.setSnmpVersion(extEntity.getSnmpVersion());
            entity.setSnmpConfig(extEntity.getSnmpConfig());
            entity.setSnmpCpuOid(extEntity.getSnmpCpuOid());
            entity.setSnmpDiskOid(extEntity.getSnmpDiskOid());
            entity.setSnmpMemOid(extEntity.getSnmpMemOid());
            entity.setCcmPort(extEntity.getCcmPort());
            entity.setCcmName(extEntity.getCcmName());
            entity.setCcmPassword(extEntity.getCcmPassword());
            entity.setAccessProtocols(extEntity.getAccessProtocols());
            entity.setAccessProtocolList(JSONArray.parseArray(extEntity.getAccessProtocols(), AccessProtocol.class));
            entity.setSnmpConfigs(JsonUtils.toSnakeObject(extEntity.getSnmpConfig(), SnmpConfig.class));
            convertModel.setExtEntity(entity);
        }
        convertModel.setExtSystemInfo(extSystemInfo);
        convertModel.setExtGateway(extGateway);
        return convertModel;
    }

    /**
     * 转换Request对象
     * @param request
     * @return
     */
    protected Device convertRequest(RealEquipmentRequest request){
        Device device = new Device();
        if (Objects.isNull(request)){
            return null;
        }
        device.setName(request.getName());
        device.setDescription(request.getDescription());
        device.setLogo(request.getLogo());
        device.setRole(request.getRole());
        device.setCategory(request.getMainCategory());
        device.setSecondCategory(request.getSubCategory());

        final ExtEntity requestExtEntity = request.getExtEntity();
        if (Objects.nonNull(requestExtEntity)) {
            ExtEntity extEntity = new ExtEntity();
            extEntity.setStyle(requestExtEntity.getStyle());
            extEntity.setParams(requestExtEntity.getParams());
            extEntity.setAccessType(requestExtEntity.getAccessType());
            extEntity.setAccessSwitchPort(requestExtEntity.getAccessSwitchPort());
            extEntity.setAccessSwitchPortName(requestExtEntity.getAccessSwitchPortName());
            extEntity.setAccessIpAddress(requestExtEntity.getAccessIpAddress());
            extEntity.setAccessMacAddress(requestExtEntity.getAccessMacAddress());
            extEntity.setSnmpVersion(requestExtEntity.getSnmpVersion());
            extEntity.setSnmpConfig(JsonUtils.toUnderlineJSONString(request.getSnmpConfig()));
            extEntity.setSnmpCpuOid(requestExtEntity.getSnmpCpuOid());
            extEntity.setSnmpDiskOid(requestExtEntity.getSnmpDiskOid());
            extEntity.setSnmpMemOid(requestExtEntity.getSnmpMemOid());
            extEntity.setCcmPort(requestExtEntity.getCcmPort());
            extEntity.setCcmName(requestExtEntity.getCcmName());
            extEntity.setCcmPassword(requestExtEntity.getCcmPassword());
            extEntity.setAccessProtocols(JSONArray.toJSONString(requestExtEntity.getAccessProtocolList()));
            device.setExtEntity(extEntity);
        }

        final ExtSystemInfo reqSystemInfo = request.getSystemInfo();
        if (Objects.nonNull(reqSystemInfo)) {
            ExtSystemInfo extSystemInfo = new ExtSystemInfo();
            extSystemInfo.setSystemType(reqSystemInfo.getSystemType());
            extSystemInfo.setSystemArchitecture(reqSystemInfo.getSystemArchitecture());
            extSystemInfo.setLanguage(reqSystemInfo.getLanguage());
            device.setExtSystemInfo(extSystemInfo);
        }
        return device;
    }

}
