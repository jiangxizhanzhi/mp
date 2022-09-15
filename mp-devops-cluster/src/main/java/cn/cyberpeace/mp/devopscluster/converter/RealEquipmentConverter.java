package cn.cyberpeace.mp.devopscluster.converter;

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
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetAuthService;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    private final CsceneSpecialService csceneSpecialService;

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


    /**
     * 根据ID查询实装信息
     * @param uuid
     * @return
     */
    public RealEquipmentBO findByUuid(String uuid) {
        Device device = deviceService.findByUuid(uuid);
        return convertDevice(device);
    }

    /**
     * 新增实装设备
     * @param request
     * @return
     */
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

    /**
     * 更新实装设备信息
     * @param request
     * @return
     */
    @Transactional
    public Device update(RealEquipmentRequest request) {
        validRealEquipmentRequest(request);
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


    /**
     * 验证请求参数
     * @param request
     */
    private void validRealEquipmentRequest(RealEquipmentRequest request) {
        if (StringUtils.isNotEmpty(request.getName()) && request.getName().length() > 126) {
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_NAME_EXCEED_MAX_LENGTH);
        }
        if (StringUtils.isNotEmpty(request.getDescription()) && request.getDescription().length() > 1000) {
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_DESCRIPTION_EXCEED_MAX_LENGTH);
        }
    }
}
