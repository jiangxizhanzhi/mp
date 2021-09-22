package cn.cyberpeace.mp.devops.cluster.converter.impl;

import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.cyberpeace.mp.devops.cluster.converter.TargetConverter;
import cn.cyberpeace.mp.devops.cluster.dto.ImagePageDTO;
import cn.cyberpeace.mp.devops.cluster.model.Device;
import cn.cyberpeace.mp.devops.cluster.model.DeviceCloudPlatformModel;
import cn.cyberpeace.mp.devops.cluster.model.TargetModel;
import cn.cyberpeace.mp.devops.cluster.repository.ImageMapper;
import cn.cyberpeace.mp.devops.cluster.request.TargetSceneRequest;
import cn.cyberpeace.mp.devops.cluster.service.DeviceService;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TargetConverterImpl implements TargetConverter {

    private ImageMapper imageMapper;

    private DeviceService deviceService;

    public TargetConverterImpl(ImageMapper imageMapper, DeviceService deviceService) {
        this.imageMapper = imageMapper;
        this.deviceService = deviceService;
    }

    @Override
    public String[] getDefaultPageIgnoreField() {
        return new String[0];
    }

    @Override
    public String[] getTotalPageIgnoreField() {
        return new String[0];
    }

    @Override
    public List<Device> allTarget(TargetSceneRequest request) {
        ImagePageDTO dto = new ImagePageDTO();
        dto.setMainCategory(request.getCategory());
        dto.setSubCategory(request.getSecondCategory());
        dto.setImageType(Objects.nonNull(request.getImageType()) ? request.getImageType().toValue() : "");
        dto.setName(request.getName());
        dto.setOwner(Objects.nonNull(UserUtils.getLoginUser()) ? UserUtils.getLoginUser().getId() : null);
        Integer total = imageMapper.totalCount(dto);
        if (total == 0){
            return null;
        }
        List<TargetModel> targetModels = imageMapper.page(dto);

        List<Integer> ids = targetModels.stream().map(TargetModel::getId).map(Integer::parseInt).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }
        DeviceCloudPlatformModel model = deviceService.findByIds(ids);

        if (Objects.isNull(model) || CollectionUtils.isEmpty(model.getItems())){
            return Lists.newArrayList();
        }

        return model.getItems();
    }
}
