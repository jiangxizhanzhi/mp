package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.base.util.BeanUtils;
import cn.cyberpeace.mp.devops.cluster.converter.RealEquipmentConverter;
import cn.cyberpeace.mp.devops.cluster.converter.model.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipment;
import cn.cyberpeace.mp.devops.cluster.converter.model.RealEquipmentConvertModel;
import cn.cyberpeace.mp.devops.cluster.converter.model.SystemInfo;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentAddRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devops.cluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RealEquipmentConverterImpl implements RealEquipmentConverter {

    private DeviceService deviceService;

    @Autowired
    public RealEquipmentConverterImpl(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return BeanUtils.getBeanGetterFieldName(RealEquipmentPageRequest::getPage,RealEquipmentPageRequest::getPerPage);
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return new String[0];
    }

    @Override
    public Integer getTotalRealEquipment() {
        return 0;
    }

    @Override
    public RealEquipmentConvertModel page(RealEquipmentPageRequest request, String... ignoreFields) {
        ignoreFields = CollectionUtils.isEmpty(Arrays.asList(ignoreFields)) ? this.getDefaultPageIgnoreField() : ignoreFields;
        String getParams = BeanUtils.wrapperGetParamsByObject(request,ignoreFields).concat("&image_type=entity");
        DeviceCloudPlatformModel model = deviceService.page(getParams);
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
    public void add(RealEquipmentAddRequest request) {

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
        Device.ExtEntity extEntity = device.getExt_entity();
        Device.ExtSystemInfo extSystemInfo = device.getExt_system_info();
        RealEquipment convertModel = RealEquipment.builder().uuid(device.getUuid())
                .name(device.getName()).mainCategory(device.getCategory()).subCategory(device.getCategory())
                .description(device.getDescription()).build();
        if (Objects.nonNull(extEntity)){
            ExtEntity entity = new ExtEntity();
            entity.setModel(extEntity.getStyle());
            entity.setParams(extEntity.getParams());
            entity.setAccessType(extEntity.getAccess_type());
            entity.setEntityStatus(extEntity.getEntity_status());
            convertModel.setExtEntity(entity);
        }
        if (Objects.nonNull(extSystemInfo)){
            SystemInfo systemInfo = SystemInfo.builder()
                    .systemType(extSystemInfo.getSystem_type()).systemArchitecture(extSystemInfo.getSystem_architecture())
                    .language(extSystemInfo.getLanguage()).build();
            convertModel.setSystemInfo(systemInfo);
        }
        return convertModel;
    }
}
