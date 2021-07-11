package cn.cyberpeace.mp.devopscluster.cloud.platform.service.impl;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.DeviceReferCsceneStat;
import cn.cyberpeace.mp.cscene.service.CsceneSpecialService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.VirtualUsb;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.VirtualUsbDownload;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.VirtualUsbStorage;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.BaseCloudPlatformResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.common.CloudPlatformItemsPageResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.BaseCpService;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.VirtualUsbService;
import cn.cyberpeace.mp.devopscluster.config.DevopsClusterConfig;
import cn.cyberpeace.mp.devopscluster.constant.DevopsClusterErrorCode;
import cn.cyberpeace.mp.devopscluster.request.VirtualUsbDeleteRequest;
import cn.cyberpeace.mp.devopscluster.request.queryparam.VirtualUsbQueryParams;
import cn.cyberpeace.mp.devopscluster.utils.FileUtils;
import cn.cyberpeace.mp.devopscluster.utils.JsonUtils;
import cn.cyberpeace.mp.devopscluster.vo.VirtualUsbVO;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.CPCLOUD_DEVICE;
import static cn.cyberpeace.mp.devopscluster.cloud.platform.constants.BaseCloudPlatformConstant.URL_VERSION;


@Service
@Slf4j
public class VirtualUsbServiceImpl extends BaseCpService implements VirtualUsbService {

    private static final String VIRTUAL_USB = "/virtual_usbs";

    private static final Integer MAX_USB_COUNT = 10;

    public VirtualUsbServiceImpl(DevopsClusterConfig devopsClusterConfig) {
        super(devopsClusterConfig);
    }

    @Autowired
    private CsceneSpecialService csceneSpecialService;

    
    @Override
    public ListObjServiceResult<VirtualUsbVO> virtualUsbPage(VirtualUsbQueryParams queryParams) {
        log.info("VirtualUsbServiceImpl.virtualUsbPage begin. queryParams:{}", queryParams);
        queryParams.ensure();
        StringBuffer clouldParams = new StringBuffer("?");
        clouldParams.append("limit=").append(queryParams.getPageSize()).append("&offset=").append(queryParams.getPage());
        if (Objects.nonNull(queryParams.getOrders())) {
            String sortKey = queryParams.getOrders()[0];
            if (sortKey.startsWith("-")) {
                sortKey = sortKey.substring(1);
                clouldParams.append("&sort_dir=").append("desc");
            } else {
                clouldParams.append("&sort_dir=").append("asc");
            }
            clouldParams.append("&sort_key=").append(sortKey);
        }
        if (StringUtils.isNotEmpty(queryParams.getName())) {
            clouldParams.append("&name=").append(queryParams.getName());
        }
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + clouldParams, CloudPlatformItemsPageResult.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + clouldParams + ". response: {}", pageResult);
        super.checkResult(pageResult);
        List<VirtualUsb> responseList = new ArrayList<>();
        Integer count = 0;
        if (Objects.nonNull(pageResult) && CollectionUtils.isNotEmpty(pageResult.getItems())) {
            count = pageResult.getTotal();
            responseList = JSONObject.parseArray(JSONObject.toJSONString(pageResult.getItems()), VirtualUsb.class);
        }
        log.info("VirtualUsbServiceImpl.virtualUsbPage end. responseList:{}", responseList);
        return new ListObjServiceResult(new ListObjResult<>(responseList, count)).asResult();
    }

    
    @Override
    public VirtualUsbVO findByUuid(String uuid) {
        log.info("VirtualUsbServiceImpl.findByUuid begin. uuid:{}", uuid);
        VirtualUsb virtualUsb = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid, VirtualUsb.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + ". response: {}", virtualUsb);
        VirtualUsbVO virtualUsbVO = new VirtualUsbVO();
        BeanUtils.copyProperties(virtualUsb, virtualUsbVO);
        log.info("VirtualUsbServiceImpl.findByUuid end. virtualUsbVO:{}", virtualUsbVO);
        return virtualUsbVO;
    }

    
    @Override
    public void add(VirtualUsbVO virtualUsbVO) {
        log.info("VirtualUsbServiceImpl.add begin. virtualUsbVO:{}", virtualUsbVO);
        StringBuffer clouldParams = new StringBuffer("?");
        clouldParams.append("limit=10").append("&offset=1");
        CloudPlatformItemsPageResult pageResult = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + clouldParams, CloudPlatformItemsPageResult.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + clouldParams + ". response: {}", pageResult);
        super.checkResult(pageResult);
        if (Objects.nonNull(pageResult) && pageResult.getTotal() >= MAX_USB_COUNT) {
            throw new MpRuntimeException(DevopsClusterErrorCode.USB_NUM_EXCEEDS_LIMIT);
        }
        VirtualUsb virtualUsb = new VirtualUsb();
        BeanUtils.copyProperties(virtualUsbVO, virtualUsb);
        BaseCloudPlatformResult result = super.sendPost(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB, JsonUtils.toUnderlineJSONString(virtualUsb), BaseCloudPlatformResult.class);
        super.checkResult(result);
        log.info("VirtualUsbServiceImpl.add end.");
    }

    
    @Override
    public void update(VirtualUsbVO virtualUsbVO) {
        log.info("VirtualUsbServiceImpl.update begin. virtualUsbVO:{}", virtualUsbVO);
        VirtualUsb virtualUsb = new VirtualUsb();
        BeanUtils.copyProperties(virtualUsbVO, virtualUsb);
        BaseCloudPlatformResult result = super.sendPut(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + virtualUsbVO.getUuid(), JsonUtils.toUnderlineJSONString(virtualUsb), BaseCloudPlatformResult.class);
        super.checkResult(result);
        log.info("VirtualUsbServiceImpl.update end.");
    }

    
    @Override
    public void delete(VirtualUsbDeleteRequest request) {
        log.info("VirtualUsbServiceImpl.delete begin. request:{}", request);
        if (CollectionUtils.isEmpty(request.getUuids())) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        this.validUsbCanDelete(request.getUuids());
        var jsonObject = new JSONObject();
        jsonObject.put("uuids", request.getUuids());
        BaseCloudPlatformResult result = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB, JSONObject.toJSONString(jsonObject), BaseCloudPlatformResult.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + ". response: {}", result);
        super.checkResult(result);
        log.info("VirtualUsbServiceImpl.delete end.");
    }

    
    @Override
    public VirtualUsbStorage fileListByUuid(String uuid) {
        log.info("VirtualUsbServiceImpl.fileListByUuid begin. uuid:{}", uuid);
        VirtualUsbStorage virtualUsbStorage = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/files", VirtualUsbStorage.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/files" + ". response: {}", virtualUsbStorage);
        log.info("VirtualUsbServiceImpl.fileListByUuid end. virtualUsbStorage:{}", virtualUsbStorage);
        return virtualUsbStorage;
    }

    
    @Override
    public BaseCloudPlatformResult upload(MultipartFile file, String uuid) {
        log.info("VirtualUsbServiceImpl.upload begin. uuid:{}", uuid);
        List<File> fileList = null;
        try {
            fileList = FileUtils.transferToFileList(Collections.singletonList(file));
            BaseCloudPlatformResult result = super.sendFormDataPost(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/files", fileList, BaseCloudPlatformResult.class);
            super.checkResult(result);
            return result;
        } catch (IOException e) {
            log.error("VirtualUsbServiceImpl.upload error",e);
        } finally {
            if (CollectionUtils.isNotEmpty(fileList)) {
                fileList.forEach(File::delete);
            }
        }
        log.info("VirtualUsbServiceImpl.upload end.");
        return null;
    }

    
    @Override
    public void deleteFile(String uuid, String fileName) {
        log.info("VirtualUsbServiceImpl.deleteFile begin. uuid:{}, fileName:{}", uuid, fileName);
        if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(fileName)) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        BaseCloudPlatformResult result = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/file/" + fileName, null, BaseCloudPlatformResult.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/file/" + fileName + ". response: {}", result);
        super.checkResult(result);
        log.info("VirtualUsbServiceImpl.deleteFile end.");
    }

    
    @Override
    public void deleteAllFiles(String uuid) {
        log.info("VirtualUsbServiceImpl.deleteAllFiles begin. uuid:{}", uuid);
        if (StringUtils.isEmpty(uuid)) {
            throw new MpRuntimeException(BaseErrorCode.PARAMS_INVALID);
        }
        BaseCloudPlatformResult result = super.sendDelete(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/files", null, BaseCloudPlatformResult.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/files" + ". response: {}", result);
        super.checkResult(result);
        log.info("VirtualUsbServiceImpl.deleteAllFiles end.");
    }

    
    @Override
    public String downloadFile(String uuid, String fileName) {
        log.info("VirtualUsbServiceImpl.downloadFile begin. uuid:{}", uuid);
        VirtualUsbDownload virtualUsbDownload = super.sendGet(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/file/" + fileName, VirtualUsbDownload.class);
        log.info(CPCLOUD_DEVICE + URL_VERSION + VIRTUAL_USB + "/" + uuid + "/file/" + fileName + ". response: {}", virtualUsbDownload);
        log.info("VirtualUsbServiceImpl.downloadFile end. virtualUsbDownload:{}", virtualUsbDownload);
        return virtualUsbDownload.getNginxFileUrl();
    }

    
    private void validUsbCanDelete(List<String> uuids) {
        for (String uuid : uuids) {
            ServiceResult<List<DeviceReferCsceneStat>> referStateServiceResult = csceneSpecialService.statVirtualUsbReferCscenes(uuid);
            ServiceResult<List<DeviceReferCsceneStat>> usedStateServiceResult = csceneSpecialService.statVirtualUsbUsedCscenes(uuid);
            List<DeviceReferCsceneStat> referStateList = referStateServiceResult.getSource();
            List<DeviceReferCsceneStat> usedStateList = usedStateServiceResult.getSource();
            List<String> names = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(referStateList)) {
                List<String> sceneNames = referStateList.stream()
                        .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                        .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                        .collect(Collectors.toList());
                names.addAll(sceneNames);
            }
            if (CollectionUtils.isNotEmpty(usedStateList)) {
                List<String> sceneNames = usedStateList.stream()
                        .filter(deviceReferCsceneStat -> StringUtils.isNotEmpty(deviceReferCsceneStat.getName()))
                        .map(deviceReferCsceneStat -> deviceReferCsceneStat.getName())
                        .collect(Collectors.toList());
                names.addAll(sceneNames);
            }
            if (CollectionUtils.isNotEmpty(names)) {
                names = names.stream().distinct().collect(Collectors.toList());
                String sceneName = StringUtils.join(names, ",");
                throw new MpRuntimeException(DevopsClusterErrorCode.USB_REFERENCED_SCENE.format(sceneName));
            }
        }
    }
}
