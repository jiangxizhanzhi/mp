 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TargetConverter.java.
 // File size 16144.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
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

/**
 * @ClassName TargetConverter
 * @Description 靶标
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TargetConverter{

    private final DeviceTargetMapper deviceTargetMapper;

    private final DeviceService deviceService;

    private final CsceneSpecialService csceneSpecialService;

    private final DevopsTargetService devopsTargetService;

    /**
     * 分页查询
     * @param dto
     * @return
     */
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

    /**
     * 查询所有靶标数据
     * @param dto
     * @return
     */
    public BasePageBO<Device> allTarget(ImagePageDTO dto) {
        BasePageBO<DeviceTargetPO> targetPageModel = this.page(dto);

        List<DeviceTargetPO> deviceTargetPOS = targetPageModel.getList();
        Map<String,DeviceTargetPO> supportExports = new HashMap<>();
        for (DeviceTargetPO targetPO : deviceTargetPOS) {
            supportExports.put(targetPO.getUuid(), targetPO);
        }

        List<Integer> ids = new ArrayList<>();
        for (DeviceTargetPO deviceTargetPO : deviceTargetPOS) {
            String id = deviceTargetPO.getId();
            Integer parseInt = Integer.parseInt(id);
            ids.add(parseInt);
        }

        BasePageBO<Device> sceneConvertModel = new BasePageBO<>();
        sceneConvertModel.setTotal(targetPageModel.getTotal());
        if (CollectionUtils.isEmpty(ids)){
            sceneConvertModel.setList(Lists.newArrayList());
            return sceneConvertModel;
        }
        List<Device> deviceList = deviceService.findByIds(ids);

        if (CollectionUtils.isEmpty(deviceList)){
            sceneConvertModel.setList(Lists.newArrayList());
            return sceneConvertModel;
        }
        Map<String, Integer> deviceIdToAttackMockTypeMap = deviceTargetPOS.stream()
                .filter(deviceTargetPO -> Objects.nonNull(deviceTargetPO.getAttackMockDeviceType()))
                .collect(Collectors.toMap(DeviceTargetPO::getId, DeviceTargetPO::getAttackMockDeviceType, (a, b) -> a));
        for (Device d : deviceList) {
            d.setAttackMockDeviceType(deviceIdToAttackMockTypeMap.get(String.valueOf(d.getId())));
            if (supportExports.containsKey(d.getUuid())) {
                d.setBehaveSupport(supportExports.get(d.getUuid()).getBehaveSupport());
                if (ImageTypeEnum.CONTAINER.toValue().equals(d.getImageType()) && d.getExtDocker() != null) {
                    d.getExtDocker().setInitSupport(supportExports.get(d.getUuid()).getInitSupport());
                }
            }
        }
        POLogoUtils.changeLogoPathToUrl(deviceList);
        sceneConvertModel.setList(deviceList);
        return sceneConvertModel;
    }

    /**
     * 查询匹配的镜像名称
     * @param dto
     * @return
     */
    public List<String> findByImagePageDTO(ImagePageDTO dto){
        BasePageBO<DeviceTargetPO> targetPageModel = this.page(dto);
        List<String> list = new ArrayList<>();
        for (DeviceTargetPO deviceTargetPO : targetPageModel.getList()) {
            String name = deviceTargetPO.getName();
            list.add(name);
        }
        return list;
    }

    /**
     * 查询常用靶标信息,支持通过名称查询
     * @return
     */
    public List<Device> getCommonTargets(String name) {
        List<Device> deviceList = new ArrayList<>();
        try {
            deviceList = deviceService.page("?limit=9999&is_internal=true&name=" + URLEncoder.encode(name, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new MpRuntimeException("encode failed");
        }
        POLogoUtils.changeLogoPathToUrl(deviceList);
        return deviceList;
    }

    /**
     * 验证靶标是否可删除
     * @param uuids
     */
    public void validTargetCanDelete(List<String> uuids) {

        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        for (DeviceTargetPO targetPO : deviceTargetPOS) {
            if (Boolean.TRUE.equals(targetPO.getIsInit())) {
                throw new MpRuntimeException(DevopsClusterErrorCode.INIT_TARGET_CAN_NOT_DELETE.format(targetPO.getName()));
            }
        }
        List<String> list = new ArrayList<>();
        for (DeviceTargetPO deviceTargetPO : deviceTargetPOS) {
            String deviceTargetPOUuid = deviceTargetPO.getUuid();
            list.add(deviceTargetPOUuid);
        }
        String[] imageUuids = list.toArray(new String[0]);
        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenesByImageUuid(uuids.toArray(new String[0]));
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        List<String> csceneNames = new ArrayList<>();
        String sceneName = StringUtils.EMPTY;

        if (MapUtils.isNotEmpty(referStateMap)) {
            for (Map.Entry<String, List<DeviceReferCsceneStat>> entry : referStateMap.entrySet()) {
                String k = entry.getKey();
                List<DeviceReferCsceneStat> v = entry.getValue();
                List<String> names = new ArrayList<>();
                for (DeviceReferCsceneStat deviceReferCsceneStat : v) {
                    if (StringUtils.isNotEmpty(deviceReferCsceneStat.getName())) {
                        String name = deviceReferCsceneStat.getName();
                        names.add(name);
                    }
                }
                csceneNames.addAll(names);
            }
            if (CollectionUtils.isNotEmpty(csceneNames)) {
                List<String> names = csceneNames.subList(0, Math.min(csceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_REFERENCED_SCENE.format(sceneName));
        }
        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageUuids);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)) {
            for (Map.Entry<String, List<DeviceReferCsceneInstanceStat>> entry : useStateMap.entrySet()) {
                String k = entry.getKey();
                List<DeviceReferCsceneInstanceStat> v = entry.getValue();
                List<String> names = new ArrayList<>();
                for (DeviceReferCsceneInstanceStat deviceReferCsceneInstanceStat : v) {
                    if (StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName())) {
                        String name = deviceReferCsceneInstanceStat.getName();
                        names.add(name);
                    }
                }
                csceneNames.addAll(names);
            }
            if (CollectionUtils.isNotEmpty(csceneNames)) {
                List<String> names = csceneNames.subList(0, Math.min(csceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_USED_SCENE.format(sceneName));
        }
        for (String uuid : uuids) {
            ServiceResult<List<DeviceReferCsceneStat>> referStateServiceUsbResult = csceneSpecialService.statEntityUsbReferCscenes(uuid);
            List<DeviceReferCsceneStat> referStateUsbList = referStateServiceUsbResult.getSource();
            if (CollectionUtils.isNotEmpty(referStateUsbList)) {
                List<String> sceneUsbNames = new ArrayList<>();
                for (DeviceReferCsceneStat deviceReferCsceneStat : referStateUsbList) {
                    if (StringUtils.isNotEmpty(deviceReferCsceneStat.getName())) {
                        String name = deviceReferCsceneStat.getName();
                        sceneUsbNames.add(name);
                    }
                }
                String sceneUsbName = StringUtils.join(sceneUsbNames, ",");
                throw new MpRuntimeException(DevopsClusterErrorCode.USB_REFERENCED_SCENE.format(sceneUsbName));
            }
        }
    }

    /**
     * validTargetCanMerge
     *
     * @param uuids
     */
    public void validTargetCanMerge(List<String> uuids) {

        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByUuids(uuids);
        List<String> list = new ArrayList<>();
        for (DeviceTargetPO deviceTargetPO : deviceTargetPOS) {
            String deviceTargetPOName = deviceTargetPO.getName();
            list.add(deviceTargetPOName);
        }
        String[] imageNames = list.toArray(new String[0]);
        //引用
        ServiceResult<Map<String, List<DeviceReferCsceneStat>>> referStateServiceResult = csceneSpecialService.statDeviceReferCscenes(imageNames);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = referStateServiceResult.getSource();

        List<String> sceneNames = new ArrayList<>();
        String sceneName = StringUtils.EMPTY;

        if (MapUtils.isNotEmpty(referStateMap)) {
            for (Map.Entry<String, List<DeviceReferCsceneStat>> entry : referStateMap.entrySet()) {
                String k = entry.getKey();
                List<DeviceReferCsceneStat> v = entry.getValue();
                List<String> names = new ArrayList<>();
                for (DeviceReferCsceneStat deviceReferCsceneStat : v) {
                    if (StringUtils.isNotEmpty(deviceReferCsceneStat.getName())) {
                        String name = deviceReferCsceneStat.getName();
                        names.add(name);
                    }
                }
                sceneNames.addAll(names);
            }
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_REFERENCED_SCENE.format(sceneName));
        }
        //使用
        ServiceResult<Map<String, List<DeviceReferCsceneInstanceStat>>> useStateServiceResult = csceneSpecialService.statDeviceReferCsceneInstances(imageNames);
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = useStateServiceResult.getSource();

        if (MapUtils.isNotEmpty(useStateMap)) {
            for (Map.Entry<String, List<DeviceReferCsceneInstanceStat>> entry : useStateMap.entrySet()) {
                String k = entry.getKey();
                List<DeviceReferCsceneInstanceStat> v = entry.getValue();
                List<String> names = new ArrayList<>();
                for (DeviceReferCsceneInstanceStat deviceReferCsceneInstanceStat : v) {
                    if (StringUtils.isNotEmpty(deviceReferCsceneInstanceStat.getName())) {
                        String name = deviceReferCsceneInstanceStat.getName();
                        names.add(name);
                    }
                }
                sceneNames.addAll(names);
            }
            if (CollectionUtils.isNotEmpty(sceneNames)) {
                List<String> names = sceneNames.subList(0, Math.min(sceneNames.size(), 3));
                sceneName = StringUtils.join(names, ",");
            }
            throw new MpRuntimeException(DevopsClusterErrorCode.IMAGE_USED_SCENE.format(sceneName));
        }
    }


    /**
     * 获取需同步的靶标ID集合
     * @param deviceIds
     * @return DeviceAsyncBO
     */
    public DeviceAsyncBO getAsyncDeviceIds(List<Integer> deviceIds){
        DeviceAsyncBO bo = new DeviceAsyncBO();
        if (CollectionUtils.isEmpty(deviceIds)){
            return new DeviceAsyncBO();
        }
        List<String> ids = new ArrayList<>();
        for (Integer integer : deviceIds) {
            String s = String.valueOf(integer);
            ids.add(s);
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.findByIds(ids);
        List<Integer> notExistedList;
        List<Integer> existedList;
        if (CollectionUtils.isEmpty(deviceTargetPOS)){
            notExistedList = deviceIds;
            existedList = Lists.newArrayList();
        }else{
            List<Integer> targetIds = new ArrayList<>();
            for (DeviceTargetPO deviceTargetPO : deviceTargetPOS) {
                Integer parseInt = Integer.parseInt(deviceTargetPO.getId());
                targetIds.add(parseInt);
            }
            List<Integer> result = new ArrayList<>();
            for (Integer deviceId : deviceIds) {
                if (!targetIds.contains(deviceId)) {
                    result.add(deviceId);
                }
            }
            notExistedList = result;
            List<Integer> list = new ArrayList<>();
            for (Integer v : deviceIds) {
                if (targetIds.contains(v)) {
                    list.add(v);
                }
            }
            existedList = list;
        }
        bo.setNotExistedList(notExistedList);
        bo.setExistedList(existedList);
        return bo;
    }

    /**
     * 分页查询靶标数据
     * @param offset
     * @param limit
     * @return
     */
    public List<Device> multiAsync(Integer offset,Integer limit){
        return deviceService.page("?offset=" + offset + "&limit=" + limit);
    }

    /**
     * 根据UUID查询靶标信息
     * @param uuid
     * @return
     */
    public Device findByUuid(String uuid){
        Device device = deviceService.findByUuid(uuid);
        device.setLogo(DefaultImgUtil.pathToFdfsUrl(device.getLogo()));
        return device;
    }
}
