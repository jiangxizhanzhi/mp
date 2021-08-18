package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.SystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.request.*;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import cn.cyberpeace.mp.devops.cluster.service.RealEquipmentService;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RealEquipmentConverterImpl implements RealEquipmentConverter {

    private RealEquipmentService realEquipmentService;

    private DeviceService deviceService;

    @Autowired
    public RealEquipmentConverterImpl(RealEquipmentService realEquipmentService,DeviceService deviceService) {
        this.realEquipmentService = realEquipmentService;
        this.deviceService = deviceService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(RealEquipmentPageRequest::getPage,RealEquipmentPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(RealEquipmentPageRequest::getOffset,
                RealEquipmentPageRequest::getPage,RealEquipmentPageRequest::getPerPage);
    }

    @Override
    public Integer getTotalRealEquipment(RealEquipmentPageRequest request) {
        String getParams = BeanUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        DeviceCloudPlatformModel page = realEquipmentService.page(getParams);
        return Objects.isNull(page) || Objects.isNull(page.getTotal()) ? NumberUtils.INTEGER_ZERO : page.getTotal();
    }

    @Override
    public RealEquipmentConvertModel page(RealEquipmentPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(Boolean.TRUE,request,ignoreFields);
        DeviceCloudPlatformModel model = realEquipmentService.page(getParams);
        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return null;
        }
        List<RealEquipment> modelList = model.getItems().stream().map(device -> convertDevice(device)).collect(Collectors.toList());

        RealEquipmentConvertModel convertModel = new RealEquipmentConvertModel();
        convertModel.setModelList(modelList);
        convertModel.setTotal(model.getTotal());
        return convertModel;
    }

    @Override
    public RealEquipment findById(String id) {
        Device device = deviceService.findById(id);
        return convertDevice(device);
    }

    @Override
    public RealEquipment add(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        if (Objects.isNull(device)){
            return new RealEquipment();
        }
        Device result = realEquipmentService.add(device);
        return convertDevice(result);
    }

    @Override
    public RealEquipment update(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        Device result = realEquipmentService.update(request.getUuid(),device);
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
        final Device.ExtEntity extEntity = device.getExtEntity();

        final ExtSystemInfo extSystemInfo = device.getExtSystemInfo();

        final ExtGateway extGateway = device.getExtGateway();

        RealEquipment convertModel = RealEquipment.builder().uuid(device.getUuid())
                .name(device.getName()).role(device.getRole()).logo(device.getLogo())
                .mainCategory(device.getCategory()).subCategory(device.getSecondCategory())
                .description(device.getDescription()).build();
        convertModel.setSuccess(device.isSuccess());
        if (Objects.nonNull(extEntity)){
            ExtEntity entity = new ExtEntity();
            entity.setId(extEntity.getId());
            entity.setDeviceUuid(extEntity.getDeviceUuid());
            entity.setModel(extEntity.getStyle());
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
            entity.setAccessProtocols(extEntity.getAccessProtocals());
            entity.setAccessProtocolList(JSONArray.parseArray(extEntity.getAccessProtocals(), ExtEntity.AccessProtocol.class));
            convertModel.setExtEntity(entity);
        }
        if (Objects.nonNull(extSystemInfo)){
            SystemInfo systemInfo = SystemInfo.builder().uuid(extSystemInfo.getUuid())
                    .systemType(extSystemInfo.getSystemType()).systemArchitecture(extSystemInfo.getSystemArchitecture())
                    .language(extSystemInfo.getLanguage()).build();
            convertModel.setSystemInfo(systemInfo);
        }

        if (Objects.nonNull(extGateway)){

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
            Device.ExtEntity extEntity = new Device.ExtEntity();
            extEntity.setStyle(requestExtEntity.getModel());
            extEntity.setParams(requestExtEntity.getParams());
            extEntity.setAccessType(requestExtEntity.getAccessType());
            extEntity.setAccessSwitchPort(requestExtEntity.getAccessSwitchPort());
            extEntity.setAccessSwitchPortName(requestExtEntity.getAccessSwitchPortName());
            extEntity.setAccessIpAddress(requestExtEntity.getAccessIpAddress());
            extEntity.setAccessMacAddress(requestExtEntity.getAccessMacAddress());
            extEntity.setSnmpVersion(requestExtEntity.getSnmpVersion());
            extEntity.setSnmpConfig(requestExtEntity.getSnmpConfig());
            extEntity.setSnmpCpuOid(requestExtEntity.getSnmpCpuOid());
            extEntity.setSnmpDiskOid(requestExtEntity.getSnmpDiskOid());
            extEntity.setSnmpMemOid(requestExtEntity.getSnmpMemOid());
            extEntity.setCcmPort(requestExtEntity.getCcmPort());
            extEntity.setCcmName(requestExtEntity.getCcmName());
            extEntity.setCcmPassword(requestExtEntity.getCcmPassword());
            extEntity.setAccessProtocals(JSONArray.toJSONString(requestExtEntity.getAccessProtocolList()));
            device.setExtEntity(extEntity);
        }

        final SystemInfo reqSystemInfo = request.getSystemInfo();
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
