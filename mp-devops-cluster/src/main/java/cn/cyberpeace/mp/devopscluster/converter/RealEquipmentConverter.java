package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.util.http.ParamUtils;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.RealEquipmentService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.bo.RealEquipmentBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetAuthService;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RealEquipmentConverter implements BaseConverter{
    private static long IN_POSITION_UPDATE_TIME = System.currentTimeMillis();

    private static ReentrantLock IN_POSITION_UPDATE_TIME_LOCK = new ReentrantLock();

    private final RealEquipmentService realEquipmentService;

    private final DeviceService deviceService;

    private final DevopsTargetService devopsTargetService;

    private final DevopsTargetAuthService devopsTargetAuthService;

    private final TargetConverter targetConverter;

    private final CsceneSpecialService csceneSpecialService;

    
    public Integer getTotalRealEquipment(RealEquipmentPageRequest request) {
        String getParams = ParamUtils.wrapperGetParamsByObject(request,this.getTotalPageIgnoreField());
        CloudPlatformPageResult<Device> page = realEquipmentService.page(getParams);
        return page.getTotal();
    }

    
    public BasePageBO<RealEquipmentBO> page(ImagePageDTO dto) {
        this.refreshIfNecessary();
        Integer total = devopsTargetService.totalCount(dto);

        BasePageBO<RealEquipmentBO> convertModel = new BasePageBO<>();
        convertModel.setTotal(0);
        if (total == 0){
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = devopsTargetService.page(dto);
        String[] imageNames = deviceTargetPOS.stream().map(DeviceTargetPO::getName).toArray(String[]::new);
        var referStateMap = csceneSpecialService.statDeviceReferCscenes(imageNames).getSource();
        var useStateMap = csceneSpecialService.statDeviceReferCsceneInstances(imageNames).getSource();

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }
        POLogoUtils.changeLogoPathToUrl(model.getList());
        List<RealEquipmentBO> modelList = model.getList().stream().map(device -> {
            RealEquipmentBO realEquipmentBO = convertDevice(device);
            List<DeviceReferCsceneStat> referStateList = new ArrayList<>();
            List<DeviceReferCsceneInstanceStat> useStateList = new ArrayList<>();
            String name = device.getName();
            if (MapUtils.isNotEmpty(referStateMap)) {
                referStateList = referStateMap.get(name);
            }
            if (MapUtils.isNotEmpty(useStateMap)) {
                useStateList = useStateMap.get(name);
            }
            realEquipmentBO.setReferenceStatus(CollectionUtils.isEmpty(referStateList) ? 0 : referStateList.size());
            realEquipmentBO.setUseStatus(CollectionUtils.isEmpty(useStateList) ? StatusEnum.NO : StatusEnum.YES);
            return realEquipmentBO;
        }).collect(Collectors.toList());

        convertModel.setList(modelList);
        convertModel.setTotal(total);
        return convertModel;
    }


    
    public RealEquipmentBO findByUuid(String uuid) {
        Device device = deviceService.findByUuid(uuid);
        return convertDevice(device);
    }

    
    @Transactional
    public Device add(RealEquipmentRequest request) {
        validRealEquipmentRequest(request);
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

    
    @Transactional
    public Device update(RealEquipmentRequest request) {
        validRealEquipmentRequest(request);
        Device device = convertRequest(request);
        Device result = realEquipmentService.update(request.getUuid(),device);
        devopsTargetService.updateById(result);
        return result;
    }

    
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

    
    protected RealEquipmentBO convertDevice(Device device){
        if (Objects.isNull(device)){
            return new RealEquipmentBO();
        }

        RealEquipmentBO convertModel = RealEquipmentBO.builder().uuid(device.getUuid())
                .name(device.getName()).role(device.getRole()).logo(DefaultImgUtil.pathToFdfsUrl(device.getLogo()))
                .mainCategory(device.getCategory()).subCategory(device.getSecondCategory())
                .description(device.getDescription()).thirdCategory(device.getL3Category()).extEntityAccess(device.getExtEntityAccess()).build();
        convertModel.setSuccess(device.isSuccess());
        convertModel.setMessage(device.getErrorMessage());
        convertModel.setExtEntity(device.getExtEntity());
        convertModel.setExtSystemInfo(device.getExtSystemInfo());
        convertModel.setExtGateway(device.getExtGateway());
        convertModel.setExtEntityUsb(device.getExtEntityUsb());
        ExtEntity extEntity = device.getExtEntity();
        if (Objects.nonNull(extEntity)){
            convertModel.setInPosition(extEntity.getEntityStatus() ? StatusEnum.YES : StatusEnum.NO);
        }
        return convertModel;
    }

    
    protected Device convertRequest(RealEquipmentRequest request){
        Device device = new Device();
        device.setName(request.getName());
        device.setDescription(request.getDescription());
        device.setLogo(DefaultImgUtil.fdfsUrlToPath(request.getLogo()));
        device.setRole(request.getRole());
        device.setCategory(request.getMainCategory());
        device.setSecondCategory(request.getSubCategory());
        device.setL3Category(Objects.nonNull(request.getThirdCategory()) ? request.getThirdCategory() : 0);
        device.setExtEntityAccess(request.getExtEntityAccess());
        device.setExtEntity(request.getExtEntity());
        device.setExtSystemInfo(request.getSystemInfo());
        device.setExtEntityUsb(request.getExtEntityUsb());
        return device;
    }


    
    private void validRealEquipmentRequest(RealEquipmentRequest request) {
        if (StringUtils.isNotEmpty(request.getName()) && request.getName().length() > 126) {
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_NAME_EXCEED_MAX_LENGTH);
        }
        if (StringUtils.isNotEmpty(request.getDescription()) && request.getDescription().length() > 1000) {
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH);
        }
    }

    private void refreshIfNecessary() {
        
        if (System.currentTimeMillis() - IN_POSITION_UPDATE_TIME > 20000) {
            
            if (IN_POSITION_UPDATE_TIME_LOCK.tryLock()) {
                try {
                    
                    ImagePageDTO refreshDto = new ImagePageDTO();
                    refreshDto.setLimit(50);
                    refreshDto.setImageType(ImageTypeEnum.ENTITY.toValue());
                    List<DeviceTargetPO> deviceTargetPOS = devopsTargetService.page(refreshDto);
                    List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(ids)) {
                        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);
                        if (model != null && CollectionUtils.isNotEmpty(model.getList())) {
                            devopsTargetService.updateInPosition(model.getList());
                        }
                    }
                    IN_POSITION_UPDATE_TIME = System.currentTimeMillis();
                } finally {
                    IN_POSITION_UPDATE_TIME_LOCK.unlock();
                }
            }
        }
    }
}
