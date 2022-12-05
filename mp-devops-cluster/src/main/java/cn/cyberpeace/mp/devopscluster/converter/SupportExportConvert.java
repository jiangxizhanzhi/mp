package cn.cyberpeace.mp.devopscluster.converter;

import cn.cyberpeace.mp.auth.util.DefaultImgUtil;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Device;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.ExtImageInfo;
import cn.cyberpeace.mp.devopscluster.cloud.platform.service.DeviceService;
import cn.cyberpeace.mp.devopscluster.converter.bo.common.BasePageBO;
import cn.cyberpeace.mp.devopscluster.converter.model.ImageConvertModel;
import cn.cyberpeace.mp.devopscluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devopscluster.enums.PublicEnum;
import cn.cyberpeace.mp.devopscluster.enums.UploadTypeEnum;
import cn.cyberpeace.mp.devopscluster.model.CloudPlatformPageResult;
import cn.cyberpeace.mp.devopscluster.model.ImageAuthTotalCount;
import cn.cyberpeace.mp.devopscluster.po.DeviceTargetPO;
import cn.cyberpeace.mp.devopscluster.repository.DeviceTargetMapper;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetAuthService;
import cn.cyberpeace.mp.devopscluster.service.DevopsTargetService;
import cn.cyberpeace.mp.devopscluster.utils.POLogoUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SupportExportConvert {

    @Resource
    private DeviceTargetMapper deviceTargetMapper;

    @Resource
    private DeviceService deviceService;

    @Resource
    private DevopsTargetAuthService devopsTargetAuthService;

    public BasePageBO<ImageConvertModel> page(ImagePageDTO dto) {
        Integer total = deviceTargetMapper.totalCount(dto);

        BasePageBO<ImageConvertModel> imageConvertPageModel = new BasePageBO<>();
        imageConvertPageModel.setTotal(total);
        if (total == 0) {
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<DeviceTargetPO> deviceTargetPOS = deviceTargetMapper.page(dto);
        Map<String, DeviceTargetPO> supportExports = new HashMap<>();
        deviceTargetPOS.stream().forEach(d -> {
            supportExports.put(d.getUuid(), d);
        });

        List<Integer> ids = deviceTargetPOS.stream().map(DeviceTargetPO::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }
        List<Device> deviceList = this.findByIds(ids);
        POLogoUtils.changeLogoPathToUrl(deviceList);

        if (CollectionUtils.isEmpty(deviceList)) {
            imageConvertPageModel.setList(Lists.newArrayList());
            return imageConvertPageModel;
        }

        List<String> targetUuids = deviceTargetPOS.stream().map(DeviceTargetPO::getUuid).collect(Collectors.toList());

        List<ImageAuthTotalCount> imageAuthTotalCounts = devopsTargetAuthService.totalCount(targetUuids);

        Map<String, Integer> imageAuthTotalCountsMap = imageAuthTotalCounts.stream().collect(Collectors.toMap(ImageAuthTotalCount::getTarget, ImageAuthTotalCount::getTotal));

        Map<String, PublicEnum> targetMap = deviceTargetPOS.stream().collect(Collectors.toMap(DeviceTargetPO::getUuid, DeviceTargetPO::getIsPublic));

        List<ImageConvertModel> modelList = deviceList.stream().map(this::wrapperImageConvertModel).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(modelList)) {
            for (ImageConvertModel imageConvertModel : modelList) {
                if (MapUtils.isNotEmpty(targetMap)) {
                    imageConvertModel.setIsPublic(targetMap.get(imageConvertModel.getUuid()));
                }
                imageConvertModel.setAuthTotal(imageAuthTotalCountsMap.get(imageConvertModel.getUuid()));
                if (supportExports.containsKey(imageConvertModel.getUuid())) {
                    imageConvertModel.setSupportExport(supportExports.get(imageConvertModel.getUuid()).getSupportExport());
                }
            }
        }

        imageConvertPageModel.setList(modelList);
        return imageConvertPageModel;
    }

    public List<Device> findByIds(List<Integer> ids) {
        CloudPlatformPageResult<Device> model = deviceService.findByIds(ids);
        return model.getList();
    }

    protected ImageConvertModel wrapperImageConvertModel(Device device){
        ImageConvertModel imageModel = new ImageConvertModel();
        imageModel.setUuid(device.getUuid());
        imageModel.setName(device.getName());
        imageModel.setLogo(DefaultImgUtil.pathToFdfsUrl(device.getLogo()));
        imageModel.setMainCategory(device.getCategory());
        imageModel.setSubCategory(device.getSecondCategory());
        imageModel.setRole(device.getRole());
        imageModel.setThirdCategory(device.getL3Category());
        imageModel.setImageType(device.getImageType());
        imageModel.setIsImport(device.getIsImport());
        imageModel.setIsInternal(device.getIsInternal());
        imageModel.setIsInit(device.getIsInit());
        imageModel.setDescription(device.getDescription());
        imageModel.setExtSystemInfo(device.getExtSystemInfo());
        ExtImageInfo extImageInfo = device.getExtImageInfo();
        if (Objects.nonNull(extImageInfo) && StringUtils.isNotBlank(extImageInfo.getUploadType())
                    && extImageInfo.getUploadType().equals(UploadTypeEnum.FTP.getDesc())){
            extImageInfo.setFtpFilename(extImageInfo.getUserFilename());
        }
        imageModel.setExtImageInfo(extImageInfo);
        imageModel.setExtVm(device.getExtVm());
        imageModel.setExtGateway(device.getExtGateway());
        imageModel.setExtDocker(device.getExtDocker());
        return imageModel;
    }
}
