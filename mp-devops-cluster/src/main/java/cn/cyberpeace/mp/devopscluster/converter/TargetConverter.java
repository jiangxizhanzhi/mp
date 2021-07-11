package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.bo.DeviceAsyncBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TargetConverter{

    private final DeviceTargetMapper deviceTargetMapper;

    private final DeviceService deviceService;

    private final CsceneSpecialService csceneSpecialService;

    private final DevopsTargetService devopsTargetService;

    
    public BasePageBO<DeviceTargetPO> page(ImagePageDTO dto) {
        Integer total = devopsTargetService.totalCount(dto);
        BasePageBO<DeviceTargetPO> targetPageModel = new BasePageBO<>();
        targetPageModel.setTotal(total);
        if (total == 0){
            targetPageModel.setList(Lists.newArrayList());
            return targetPageModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);
        POLogoUtils.changeLogoPathToUrl(deviceTargetPOS);
        targetPageModel.setList(deviceTargetPOS);
        return targetPageModel;
    }

    
    public BasePageBO<Device> allTarget(ImagePageDTO dto) {
        BasePageBO<DeviceTargetPO> targetPageModel = this.page(dto);

        List<DeviceTargetPO> deviceTargetPOS = targetPageModel.getList();
        Map<String,DeviceTargetPO> supportExports = new HashMap<>();
        deviceTargetPOS.stream().forEach(d -> {
            supportExports.put(d.getUuid(), d);
        });

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());

        BasePageBO<Device> sceneConvertModel = new BasePageBO<>();
        sceneConvertModel.setTotal(targetPageModel.getTotal());
        if (CollectionUtils.isEmpty(ids)){
            sceneConvertModel.setList(Lists.newArrayList());
            return sceneConvertModel;
        }
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getList())){
            sceneConvertModel.setList(Lists.newArrayList());
            return sceneConvertModel;
        }
        for (var d : model.getList()) {
            if (ImageTypeEnum.CONTAINER.toValue().equals(d.getImageType()) && supportExports.containsKey(d.getUuid())) {
                if (d.getExtDocker() != null) {
                    d.getExtDocker().setInitSupport(supportExports.get(d.getUuid()).getInitSupport());
                }
            }
        }
        POLogoUtils.changeLogoPathToUrl(model.getList());
        sceneConvertModel.setList(model.getList());
        return sceneConvertModel;
    }

    
    public List<String> findByImagePageDTO(ImagePageDTO dto){
        BasePageBO<DeviceTargetPO> targetPageModel = this.page(dto);
        return targetPageModel.getList().stream().map(DeviceTargetPO::getName).collect(Collectors.toList());
    }

    
    public List<Device> getCommonTargets(String name) {
        CloudPlatformPageResult<Device> model = null;
        try {
            model = deviceService.page("?limit=9999&is_internal=true&name=" + URLEncoder.encode(name, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new MpRuntimeException("encode failed");
        }
        List<Device> deviceList = model.getList();
        POLogoUtils.changeLogoPathToUrl(deviceList);
        return deviceList;
    }

    
    public void validTargetCanDelete(List<String> uuids) {

        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        deviceTargetPOS.forEach(deviceTargetPO -> {
            if (Boolean.TRUE.equals(deviceTargetPO.getIsInit())) {
                throw new MpRuntimeException(DevopsClusterErrorCode.INIT_TARGET_CAN_NOT_DELETE.format(deviceTargetPO.getName()));
            }
        });
        String[] imageNames = deviceTargetPOS.stream().map(DeviceTargetPO::getName).toArray(String[]::new);
        
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        List<String> sceneNames = new ArrayList<>();
        String sceneName = StringUtils.EMPTY;

        if (MapUtils.isNotEmpty(referStateMap)) {
            referStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                        .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_REFERENCED_SCENE.format(sceneName));
        }
        
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageNames);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)) {
            useStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneInstanceStat -> StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName()))
                        .map(deviceReferCsceneInstanceStat -> deviceReferCsceneInstanceStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_USED_SCENE.format(sceneName));
        }
        for (String uuid : uuids) {
            ServiceResult<List<DeviceReferCsceneStat>> referStateServiceUsbResult = csceneSpecialService.statEntityUsbReferCscenes(uuid);
            List<DeviceReferCsceneStat> referStateUsbList = referStateServiceUsbResult.getSource();
            if (CollectionUtils.isNotEmpty(referStateUsbList)) {
                List<String> sceneUsbNames = referStateUsbList.stream()
                        .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                        .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                        .collect(Collectors.toList());
                String sceneUsbName = StringUtils.join(sceneUsbNames, ",");
                throw new MpRuntimeException(DevopsClusterErrorCode.USB_REFERENCED_SCENE.format(sceneUsbName));
            }
        }
    }

    public void validTargetCanMerge(List<String> uuids) {

        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        String[] imageNames = deviceTargetPOS.stream().map(DeviceTargetPO::getName).toArray(String[]::new);
        
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        List<String> sceneNames = new ArrayList<>();
        String sceneName = StringUtils.EMPTY;

        if (MapUtils.isNotEmpty(referStateMap)) {
            referStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                        .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_REFERENCED_SCENE.format(sceneName));
        }
        
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageNames);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)) {
            useStateMap.forEach((k, v) -> {
                var names = v.stream()
                        .filter(deviceReferCsceneInstanceStat -> StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName()))
                        .map(deviceReferCsceneInstanceStat -> deviceReferCsceneInstanceStat.getName())
                        .collect(Collectors.toList());
                sceneNames.addAll(names);
            });
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_USED_SCENE.format(sceneName));
        }
    }


    
    public DeviceAsyncBO getAsyncDeviceIds(List<Integer> deviceIds){
        DeviceAsyncBO bo = new DeviceAsyncBO();
        if (CollectionUtils.isEmpty(deviceIds)){
            return new DeviceAsyncBO();
        }
        List<String> ids = deviceIds.stream().map(String::valueOf).collect(Collectors.toList());
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByIds(ids);
        List<Integer> notExistedList;
        List<Integer> existedList;
        if (CollectionUtils.isEmpty(deviceTargetPOS)){
            notExistedList = deviceIds;
            existedList = Lists.newArrayList();
        }else{
            List<Integer> targetIds = deviceTargetPOS.stream().map(v -> Integer.parseInt(v.getId())).collect(Collectors.toList());
            notExistedList = deviceIds.stream().filter(v -> !targetIds.contains(v)).collect(Collectors.toList());
            existedList = deviceIds.stream().filter(v -> targetIds.contains(v)).collect(Collectors.toList());
        }
        bo.setNotExistedList(notExistedList);
        bo.setExistedList(existedList);
        return bo;
    }

    
    public List<Device> multiAsync(Integer offset,Integer limit){
        CloudPlatformPageResult<Device> model = deviceService.page("?offset=" + offset + "&limit=" + limit);
        return model.getList();
    }

    
    public Device findByUuid(String uuid){
        Device device = deviceService.findByUuid(uuid);
        device.setLogo(DefaultImgUtil.pathToFdfsUrl(device.getLogo()));
        return device;
    }
}
