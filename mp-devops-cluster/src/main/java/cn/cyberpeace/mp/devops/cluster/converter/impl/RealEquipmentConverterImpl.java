package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.SystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
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
    public void update(RealEquipmentRequest request) {
        Device device = convertRequest(request);
        realEquipmentService.update(request.getUuid(),device);
    }

    @Override
    public void delete(RealEquipmentDeleteRequest request) {
        deviceService.deleteByIds(request.getUuids());
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
        Device.ExtEntity extEntity = device.getExt_entity();

        ExtSystemInfo extSystemInfo = device.getExt_system_info();

        RealEquipment convertModel = RealEquipment.builder().uuid(device.getUuid())
                .name(device.getName()).role(device.getRole()).logo(device.getLogo())
                .mainCategory(device.getCategory()).subCategory(device.getSecond_category())
                .description(device.getDescription()).build();
        convertModel.setSuccess(device.isSuccess());
        if (Objects.nonNull(extEntity)){
            ExtEntity entity = new ExtEntity();
            entity.setId(extEntity.getId());
            entity.setDeviceUuid(extEntity.getDevice_uuid());
            entity.setModel(extEntity.getStyle());
            entity.setParams(extEntity.getParams());
            entity.setAccessType(extEntity.getAccess_type());
            entity.setEntityStatus(extEntity.getEntity_status());
            entity.setAccessSwitchPort(extEntity.getAccess_switch_port());
            entity.setAccessSwitchPortName(extEntity.getAccess_switch_port_name());
            entity.setAccessIpAddress(extEntity.getAccess_ip_address());
            entity.setAccessMacAddress(extEntity.getAccess_mac_address());
            entity.setSnmpVersion(extEntity.getSnmp_version());
            entity.setSnmpConfig(extEntity.getSnmp_config());
            entity.setSnmpCpuOid(extEntity.getSnmp_cpu_oid());
            entity.setSnmpDiskOid(extEntity.getSnmp_disk_oid());
            entity.setSnmpMemOid(extEntity.getSnmp_mem_oid());
            entity.setCcmPort(extEntity.getCcm_port());
            entity.setCcmName(extEntity.getCcm_name());
            entity.setCcmPassword(extEntity.getCcm_password());
            entity.setAccessProtocols(extEntity.getAccess_protocals());
            entity.setAccessProtocolList(JSONArray.parseArray(extEntity.getAccess_protocals(), ExtEntity.AccessProtocol.class));
            convertModel.setExtEntity(entity);
        }
        if (Objects.nonNull(extSystemInfo)){
            SystemInfo systemInfo = SystemInfo.builder().uuid(extSystemInfo.getUuid())
                    .systemType(extSystemInfo.getSystem_type()).systemArchitecture(extSystemInfo.getSystem_architecture())
                    .language(extSystemInfo.getLanguage()).build();
            convertModel.setSystemInfo(systemInfo);
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
        device.setSecond_category(request.getSubCategory());

        final ExtEntity requestExtEntity = request.getExtEntity();
        if (Objects.nonNull(requestExtEntity)) {
            Device.ExtEntity extEntity = new Device.ExtEntity();
            extEntity.setStyle(requestExtEntity.getModel());
            extEntity.setParams(requestExtEntity.getParams());
            extEntity.setAccess_type(requestExtEntity.getAccessType());
            extEntity.setAccess_switch_port(requestExtEntity.getAccessSwitchPort());
            extEntity.setAccess_switch_port_name(requestExtEntity.getAccessSwitchPortName());
            extEntity.setAccess_ip_address(requestExtEntity.getAccessIpAddress());
            extEntity.setAccess_mac_address(requestExtEntity.getAccessMacAddress());
            extEntity.setSnmp_version(requestExtEntity.getSnmpVersion());
            extEntity.setSnmp_config(requestExtEntity.getSnmpConfig());
            extEntity.setSnmp_cpu_oid(requestExtEntity.getSnmpCpuOid());
            extEntity.setSnmp_disk_oid(requestExtEntity.getSnmpDiskOid());
            extEntity.setSnmp_mem_oid(requestExtEntity.getSnmpMemOid());
            extEntity.setCcm_port(requestExtEntity.getCcmPort());
            extEntity.setCcm_name(requestExtEntity.getCcmName());
            extEntity.setCcm_password(requestExtEntity.getCcmPassword());
            extEntity.setAccess_protocals(JSONArray.toJSONString(requestExtEntity.getAccessProtocolList()));
            device.setExt_entity(extEntity);
        }

        final SystemInfo reqSystemInfo = request.getSystemInfo();
        if (Objects.nonNull(reqSystemInfo)) {
            ExtSystemInfo extSystemInfo = new ExtSystemInfo();
            extSystemInfo.setSystem_type(reqSystemInfo.getSystemType());
            extSystemInfo.setSystem_architecture(reqSystemInfo.getSystemArchitecture());
            extSystemInfo.setLanguage(reqSystemInfo.getLanguage());
            device.setExt_system_info(extSystemInfo);
        }
        return device;
    }

}
