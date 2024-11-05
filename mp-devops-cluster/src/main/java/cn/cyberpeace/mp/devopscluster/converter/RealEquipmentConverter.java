 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name RealEquipmentConverter.java.
 // File size 18460.
 // File create date Fri Jul 12 12:55:29 CST 2024.
 // File update date Fri Jul 12 12:55:29 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneInstanceStat;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.model.CsceneCommonCollectModel;
import cn.cyberpeace.mp.cscene.repository.CsceneCommonCollectMapper;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.RealEquipmentService;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.converter.bo.RealEquipmentBO;
import cn.cyberpeace.mp.devopscluster.converter.bo.SceneRealEquipmentBo;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.dto.ImageDeleteDTO;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.ImageTypeEnum;
import cn.cyberpeace.mp.devopscluster.enums.StatusEnum;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentPageRequest;
import cn.cyberpeace.mp.devopscluster.request.RealEquipmentRequest;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetAuthService;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @ClassName RealEquipmentConverter
 * @Description 实装管理
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseConverter
 */
@Slf4j
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

    private final CsceneCommonCollectMapper csceneCommonCollectMapper;


    public static final String SYSLOG = "syslog";

    public static final String KAFKA = "kafka";

    public static final String RESTAPI = "restapi";

    public static final String WEBSERVICE = "webservice";

    /**
     * 获取实装总数
     * @param request
     * @return
     */
    public Integer getTotalRealEquipment(RealEquipmentPageRequest request) {
        ImagePageDTO dto = new ImagePageDTO().setSearchType(2).setInPosition(request.getInPosition())
                .setName(request.getName()).setUseStatus(request.getUseStatus())
                .setMainCategory(request.getCategory()).setThirdCategory(request.getThirdCategory());
        return devopsTargetService.totalCount(dto);
    }

    /**
     * 分页查询实装列表
     * @param dto
     * @return
     */
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
        List<String> list = new ArrayList<>();
        for (DeviceTargetPO targetPO : deviceTargetPOS) {
            String deviceTargetPO1Name = targetPO.getName();
            list.add(deviceTargetPO1Name);
        }
        String[] imageNames = list.toArray(new String[0]);
        Map<String, List<DeviceReferCsceneStat>> referStateMap = csceneSpecialService.statDeviceReferCscenes(imageNames).getSource();
        Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap = csceneSpecialService.statDeviceReferCsceneInstances(imageNames).getSource();

        List<Integer> ids = new ArrayList<>();
        for (DeviceTargetPO deviceTargetPO : deviceTargetPOS) {
            String id = deviceTargetPO.getId();
            Integer parseInt = Integer.parseInt(id);
            ids.add(parseInt);
        }
        if (CollectionUtils.isEmpty(ids)){
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }
        List<Device> deviceList = deviceService.findByIds(ids);

        if (CollectionUtils.isEmpty(deviceList)){
            convertModel.setList(Lists.newArrayList());
            return convertModel;
        }
        POLogoUtils.changeLogoPathToUrl(deviceList);
        List<RealEquipmentBO> modelList = new ArrayList<>();
        for (Device device : deviceList) {
            RealEquipmentBO realEquipmentBO = getRealEquipmentBO(referStateMap, useStateMap, device);
            modelList.add(realEquipmentBO);
        }

        convertModel.setList(modelList);
        convertModel.setTotal(total);
        return convertModel;
    }

    /**
     * getRealEquipmentBO
     *
     * @param referStateMap
     * @param useStateMap
     * @param device
     * @return
     */
    private RealEquipmentBO getRealEquipmentBO(Map<String, List<DeviceReferCsceneStat>> referStateMap, Map<String, List<DeviceReferCsceneInstanceStat>> useStateMap, Device device) {
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
        result.setOpenSnmp(request.getOpenSnmp());
        result.setOpenRestapi(request.getOpenRestapi());
        result.setOpenKafka(request.getOpenKafka());
        result.setOpenSoap(request.getOpenSoap());
        result.setOpenSyslog(request.getOpenSyslog());
        result.setEntityJumpUrl(request.getEntityJumpUrl());
        result.setEntityPassword(request.getEntityPassword());
        result.setEntityUserName(request.getEntityUserName());
        devopsTargetService.updateById(result, request.getExtEntityAccess());
        return result;
    }

    /**
     * 批量删除实装设备信息
     * @param request
     * @return
     */
    @Transactional
    public BaseCloudPlatformResult delete(RealEquipmentDeleteRequest request) {

        // 删除云平台数据
        targetConverter.validTargetCanDelete(request.getUuids());
        CloudPlatformResult result = deviceService.deleteByIds(request.getUuids());

        // 删除云平台数据成功后删除本地数据
        if (null != result.getData()) {
            JSONObject jsonObject = JSONObject.parseObject(JSONArray.toJSONString(result.getData()));
            if ("true".equals(jsonObject.getString("success"))) {
                ImageDeleteDTO dto = new ImageDeleteDTO();
                dto.setIds(request.getUuids());
                devopsTargetService.delete(dto);
                devopsTargetAuthService.deleteByTargets(request.getUuids());
            }
        }
        return result;
    }

    /**
     * report
     *
     * @param ip
     * @param content
     * @param agreement
     */
    public void report(String ip, String content, String agreement) {
        if (StringUtils.isEmpty(ip)) {
            throw new MpRuntimeException("ip为空");
        }

        SceneRealEquipmentBo realEquipmentBo = getRealEquipmentId(ip);
        if (realEquipmentBo == null) {
            return;
        }

        switch (agreement) {
            case SYSLOG:
                if (!realEquipmentBo.getOpenSyslog()) {
                    return;
                }
                break;
            case KAFKA:
                if (!realEquipmentBo.getOpenKafka()) {
                    return;
                }
                break;
            case RESTAPI:
                if (!realEquipmentBo.getOpenRestapi()) {
                    return;
                }
                break;
            case WEBSERVICE:
                if (!realEquipmentBo.getOpenSoap()) {
                    return;
                }
                break;
        }
        CsceneCommonCollectModel model = new CsceneCommonCollectModel();
        model.setAgreement(agreement);
        model.setDetail(content);
        model.setCsceneId(realEquipmentBo.getSceneId());
        model.setHostName(realEquipmentBo.getName());
        model.setNodeId(realEquipmentBo.getNodeId());
        model.setTime(LocalDateTime.now());
        csceneCommonCollectMapper.create(model);
    }

    /**
     * report
     *
     * @param request
     * @param content
     */
    public void report(HttpServletRequest request, String content) {
        String ip = getClientIp(request);
        report(ip, content, RESTAPI);
    }

    /**
     * getClientIp
     *
     * @param request
     * @return
     */
    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
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
        convertModel.setOpenSyslog(device.getOpenSyslog());
        convertModel.setOpenKafka(device.getOpenKafka());
        convertModel.setOpenRestapi(device.getOpenRestapi());
        convertModel.setOpenSoap(device.getOpenSoap());
        convertModel.setOpenSnmp(device.getOpenSnmp());
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
        device.setLogo(DefaultImgUtil.fdfsUrlToPath(request.getLogo()));
        device.setRole(request.getRole());
        device.setCategory(request.getMainCategory());
        device.setSecondCategory(request.getSubCategory());
        device.setL3Category(Objects.nonNull(request.getThirdCategory()) ? request.getThirdCategory() : 0);
        device.setExtEntityAccess(request.getExtEntityAccess());
        device.setExtEntity(request.getExtEntity());
        device.setExtSystemInfo(request.getSystemInfo());
        device.setExtEntityUsb(request.getExtEntityUsb());
        device.setOpenSyslog(request.getOpenSyslog());
        device.setOpenKafka(request.getOpenKafka());
        device.setOpenRestapi(request.getOpenRestapi());
        device.setOpenSoap(request.getOpenSoap());
        device.setOpenSnmp(request.getOpenSnmp());
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

    /**
     * refreshIfNecessary
     */
    private void refreshIfNecessary() {
        // 20秒没更新 尝试更新
//        if (System.currentTimeMillis() - IN_POSITION_UPDATE_TIME > 20000) {
//            //  拿到锁就更新下,没拿到就使用旧的数据
//            if (IN_POSITION_UPDATE_TIME_LOCK.tryLock()) {
//                try {
//                    // 刷新一下在位状态
//                    ImagePageDTO refreshDto = new ImagePageDTO();
//                    refreshDto.setLimit(50);
//                    refreshDto.setImageType(ImageTypeEnum.ENTITY.toValue());
//                    List<DeviceTargetPO> deviceTargetPOS = devopsTargetService.page(refreshDto);
//                    List<Integer> ids = new ArrayList<>();
//                    for (DeviceTargetPO deviceTargetPO : deviceTargetPOS) {
//                        String id = deviceTargetPO.getId();
//                        Integer parseInt = Integer.parseInt(id);
//                        ids.add(parseInt);
//                    }
//                    if (CollectionUtils.isNotEmpty(ids)) {
//                        List<Device> deviceList = deviceService.findByIds(ids);
//                        if (CollectionUtils.isNotEmpty(deviceList)) {
//                            devopsTargetService.updateInPosition(deviceList);
//                        }
//                    }
//                    IN_POSITION_UPDATE_TIME = System.currentTimeMillis();
//                } finally {
//                    IN_POSITION_UPDATE_TIME_LOCK.unlock();
//                }
//            }
//        }
    }


    /**
     * 根据ip获取场景中的实装设备信息
     * @param ip
     * @return
     */
    private SceneRealEquipmentBo getRealEquipmentId(String ip) {
        List<String> ids = devopsTargetService.getAllRealEquipmentId();
        List<Device> deviceList = deviceService.findByIds(ids.stream().map(Integer::parseInt).collect(Collectors.toList()));
        for (Device device : deviceList) {
            if (device.getExtEntity().getSnmpConfig() != null) {
                if (ip.equals(device.getExtEntity().getSnmpConfig().getIp())) {
                    return devopsTargetService.getBoById(Integer.toString(device.getId()));
                }
            }
        }
        log.error("找不到对应的实体设备");
        return null;
    }
}
